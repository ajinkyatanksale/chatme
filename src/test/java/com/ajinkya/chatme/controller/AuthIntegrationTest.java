package com.ajinkya.chatme.controller;

import com.ajinkya.chatme.dto.auth.AuthResponse;
import com.ajinkya.chatme.dto.auth.LoginRequest;
import com.ajinkya.chatme.dto.auth.RegistrationRequest;
import com.ajinkya.chatme.dto.error.ErrorResponse;
import com.ajinkya.chatme.dto.room.RoomResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ActiveProfiles("test")
public class AuthIntegrationTest extends BaseIntegrationTest {

    @LocalServerPort
    int port;

    @Test
    public void registerAndLoginFullFlow() {
        RestClient restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();

        // Extract credentials to variables to guarantee they match exactly
        String testUsername = "testuser123";
        String testPassword = "securepassword";

        // 1. REGISTER
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .username(testUsername)
                .email("testuser@def.com")
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

        // 2. LOGIN
        LoginRequest loginRequest = LoginRequest.builder()
                .username(testUsername)
                .password(testPassword)
                .build();

        ResponseEntity<AuthResponse> loginResponse = restClient.post()
                .uri("/chat-me/login")
                .body(loginRequest)
                .retrieve()
                .toEntity(AuthResponse.class);

        assert loginResponse.getStatusCode() == HttpStatus.OK;
        assert Objects.requireNonNull(loginResponse.getBody()).getToken() != null;

        // 3. GET ROOMS
        String token = loginResponse.getBody().getToken();

        ResponseEntity<RoomResponse> roomsResponse = restClient.get()
                .uri("/chat-me/room/rooms")
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .toEntity(RoomResponse.class);

        assert roomsResponse.getStatusCode() == HttpStatus.OK;
    }

    @Test
    public void registerDuplicateEmail() {
        RestClient restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();

        // Extract credentials to variables to guarantee they match exactly
        String testUsername = "testuser12";
        String testPassword = "securepassword";

        // 1. REGISTER
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .username(testUsername)
                .email("testuser1@def.com")
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

        ResponseEntity<ErrorResponse> registerResponse1 = restClient.post()
                .uri("/chat-me/register")
                .body(registrationRequest)
                .retrieve()
                .toEntity(ErrorResponse.class);

        assertEquals(HttpStatus.IM_USED, registerResponse1.getStatusCode());
    }

    @Test
    public void registerAndLoginFullFlowFailure() {
        RestClient restClient = RestClient.builder().baseUrl("http://localhost:" + port).build();

        // Extract credentials to variables to guarantee they match exactly
        String testUsername = "testuser1234";
        String testPassword = "securepassword";

        // 1. REGISTER
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .username(testUsername)
                .email("testuser2@def.com")
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

        // 2. LOGIN
        LoginRequest loginRequest = LoginRequest.builder()
                .username(testUsername)
                .password("wdqdwqdqwdqw")
                .build();

        HttpClientErrorException exception = assertThrows(HttpClientErrorException.class, () -> {
            restClient.post()
                    .uri("/chat-me/login")
                    .body(loginRequest)
                    .retrieve()
                    .toEntity(ErrorResponse.class);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());

    }
}
