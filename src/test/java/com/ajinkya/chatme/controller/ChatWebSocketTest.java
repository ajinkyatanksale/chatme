package com.ajinkya.chatme.controller;

import com.ajinkya.chatme.dto.auth.AuthResponse;
import com.ajinkya.chatme.dto.auth.RegistrationRequest;
import com.ajinkya.chatme.dto.message.MessageRequest;
import com.ajinkya.chatme.dto.message.MessageResponse;
import com.ajinkya.chatme.dto.room.CreateRoomRequest;
import com.ajinkya.chatme.dto.room.JoinRoomResponse;
import com.ajinkya.chatme.dto.room.RoomResponse;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.converter.JacksonJsonMessageConverter;
import org.springframework.messaging.simp.stomp.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.Transport;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
public class ChatWebSocketTest extends BaseIntegrationTest {

    @LocalServerPort
    int port;

    WebSocketStompClient webSocketStompClient;

    @BeforeEach
    public void setup() {
        List<Transport> transports = List.of(new WebSocketTransport(new StandardWebSocketClient()));
        SockJsClient sockJsClient = new SockJsClient(transports);
        webSocketStompClient = new WebSocketStompClient(sockJsClient);
        webSocketStompClient.setMessageConverter(new JacksonJsonMessageConverter());
    }

    @Test
    public void sendMessageBroadcastTest() throws InterruptedException, ExecutionException, TimeoutException {
        RestClient restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();
        String token1 = registerUser("testuser001", "testuser001@gmail.com", restClient);
        String token2 = registerUser("testuser002", "testuser002@gmail.com", restClient);

        CreateRoomRequest createRoomRequest = CreateRoomRequest.builder()
                .name("Ksharu")
                .RoomType("DM")
                .build();

        ResponseEntity<String> createRoomResponse = restClient.post()
                .uri("/chat-me/room/")
                .header("Authorization", "Bearer " + token1)
                .body(createRoomRequest)
                .retrieve()
                .toEntity(String.class);

        assert createRoomResponse.getStatusCode() == HttpStatus.OK;
        assertEquals(createRoomResponse.getBody(), "Room created successfully!");

        ResponseEntity<RoomResponse> roomsResponse = restClient.get()
                .uri("/chat-me/room/")
                .header("Authorization", "Bearer " + token1)
                .retrieve()
                .toEntity(RoomResponse.class);

        assert roomsResponse.getStatusCode() == HttpStatus.OK;
        assert !Objects.requireNonNull(roomsResponse.getBody()).getRooms().isEmpty();

        UUID roomId = roomsResponse.getBody().getRooms().getFirst().getRoomId();

        ResponseEntity<JoinRoomResponse> joinRoomResponse = restClient.post()
                .uri("/chat-me/room/" + roomId)
                .header("Authorization", "Bearer " + token2)
                .body("")
                .retrieve()
                .toEntity(JoinRoomResponse.class);
        log.info(joinRoomResponse.toString());
        assert roomsResponse.getStatusCode() == HttpStatus.OK;
        assert Objects.requireNonNull(joinRoomResponse.getBody()).getMessage().equals("Room joining successful");

        BlockingQueue<MessageResponse> received = new LinkedBlockingDeque<>();
        String wsUrl1 = String.format("ws://localhost:%d/chatme?token=%s", port, token1);
        String wsUrl2 = String.format("ws://localhost:%d/chatme?token=%s", port, token2);

        StompSessionHandler sessionHandler = new StompSessionHandlerAdapter() {
            @Override
            public void afterConnected(StompSession session, @NotNull StompHeaders connectedHeaders) {
                session.subscribe("/topic/room/" + roomId, new StompFrameHandler() {
                    @Override
                    public @NotNull Type getPayloadType(@NotNull StompHeaders headers) {
                        return MessageResponse.class;
                    }
                    @Override
                    public void handleFrame(@NotNull StompHeaders headers, Object payload) {
                        received.offer((MessageResponse) payload);
                    }
                });
            }

            @Override
            public void handleException(@NotNull StompSession session, StompCommand command, @NotNull StompHeaders headers, byte @NotNull [] payload, Throwable exception) {
                log.error("STOMP Exception: {}", exception.getMessage());
            }
        };

        StompSession sessionUser1 = webSocketStompClient.connectAsync(wsUrl2, sessionHandler)
                .get(5, TimeUnit.SECONDS);

        Thread.sleep(500);

        StompSession sessionUser2 = webSocketStompClient.connectAsync(wsUrl1, sessionHandler)
                .get(5, TimeUnit.SECONDS);

        MessageRequest messageToSend = MessageRequest.builder()
                        .content("Hi Ksharu")
                        .contentType("TEXT")
                        .build();
        sessionUser2.send("/app/chat/" + roomId, messageToSend);

        MessageResponse msg = received.poll(5, TimeUnit.SECONDS);
        assertNotNull(msg);
        assertEquals("Hi Ksharu", msg.getContent());
        sessionUser2.disconnect();
        sessionUser1.disconnect();
    }

    private String registerUser(String username, String email, RestClient restClient) {
        String testPassword = "securepassword";
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .username(username)
                .email(email)
                .password(testPassword)
                .name("Test User")
                .build();

        ResponseEntity<AuthResponse> registerResponse = restClient.post()
                .uri("/chat-me/register")
                .body(registrationRequest)
                .retrieve()
                .toEntity(AuthResponse.class);

        return Objects.requireNonNull(registerResponse.getBody()).getToken();
    }

}
