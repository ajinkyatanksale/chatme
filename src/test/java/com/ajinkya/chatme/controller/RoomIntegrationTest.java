package com.ajinkya.chatme.controller;

/*
*
* createRoom_andSendMessage_fullFlow
→ register user, get token
→ POST /api/rooms → assert room created
→ GET /api/rooms → assert room in list
→ GET /api/rooms/{id}/messages → assert empty page
* */

import com.ajinkya.chatme.dto.auth.AuthResponse;
import com.ajinkya.chatme.dto.auth.RegistrationRequest;
import com.ajinkya.chatme.dto.message.MessageHistoryResponse;
import com.ajinkya.chatme.dto.room.CreateRoomRequest;
import com.ajinkya.chatme.dto.room.RoomResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RoomIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort
    int port;

    @Test
    public void roomFlow() {
        RestClient restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();

        // Extract credentials to variables to guarantee they match exactly
        String testUsername = "testuser12345";
        String testPassword = "securepassword";

        // 1. REGISTER
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .username(testUsername)
                .email("testuser12345@def.com")
                .password(testPassword)
                .name("Test User")
                .build();

        ResponseEntity<AuthResponse> registerResponse = restClient.post()
                .uri("/chat-me/register")
                .body(registrationRequest)
                .retrieve()
                .toEntity(AuthResponse.class);

        assert registerResponse.getStatusCode() == HttpStatus.OK;
        assert Objects.requireNonNull(registerResponse.getBody()).getToken() != null;

        String token = registerResponse.getBody().getToken();
        log.info(token);

        CreateRoomRequest createRoomRequest = CreateRoomRequest.builder()
                .name("Ksharu")
                .RoomType("DM")
                .build();

        ResponseEntity<String> createRoomResponse = restClient.post()
                .uri("/chat-me/room/")
                .header("Authorization", "Bearer " + token)
                .body(createRoomRequest)
                .retrieve()
                .toEntity(String.class);

        assert createRoomResponse.getStatusCode() == HttpStatus.OK;
        Assertions.assertEquals(createRoomResponse.getBody(), "Room created successfully!");

        ResponseEntity<RoomResponse> roomsResponse = restClient.get()
                .uri("/chat-me/room/")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(RoomResponse.class);

        assert roomsResponse.getStatusCode() == HttpStatus.OK;
        assert !Objects.requireNonNull(roomsResponse.getBody()).getRooms().isEmpty();

        UUID roomId = roomsResponse.getBody().getRooms().getFirst().getRoomId();
        ResponseEntity<MessageHistoryResponse> messageResponse = restClient.get()
                .uri("/chat-me/room/" + roomId + "/message?pagenumber=0&pagesize=4")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(MessageHistoryResponse.class);

        assert messageResponse.getStatusCode() == HttpStatus.OK;
        assert Objects.requireNonNull(messageResponse.getBody()).getMessages().isEmpty();
    }
}
