package com.ajinkya.chatme.service;

import com.ajinkya.chatme.dto.auth.AuthResponse;
import com.ajinkya.chatme.dto.auth.LoginRequest;
import com.ajinkya.chatme.dto.auth.RegistrationRequest;
import com.ajinkya.chatme.entity.User;
import com.ajinkya.chatme.repository.UserRepository;
import com.ajinkya.chatme.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;


@Slf4j
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    AuthService authService;

    @Mock
    UserRepository userRepository;

    @Mock
    PasswordEncoder passwordEncoder;

    @Mock
    JwtService jwtService;

    @Mock
    AuthenticationManager authenticationManager;

    @Test
    public void registerSuccessTest() {
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .name("ABC DEF")
                .email("abc@gmail.com")
                .username("abc")
                .password("abc@1234")
                .build();

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(jwtService.generateToken(any(String.class))).thenReturn("abcd");

        AuthResponse authResponse = authService.register(registrationRequest);

        assertNotNull(authResponse);
        assert(authResponse.getToken().equals("abcd"));
        assert(authResponse.getMessage().equals("Registration Successful"));
    }

    public void registerSuccessAlreadyExists() {
        RegistrationRequest registrationRequest = RegistrationRequest.builder()
                .name("ABC DEF")
                .email("abc@gmail.com")
                .username("abc")
                .password("abc@1234")
                .build();

        when(userRepository.findByUsername(any(String.class))).thenReturn(Optional.of(new User()));

        AuthResponse authResponse = authService.register(registrationRequest);

        assertNull(authResponse);
    }

    @Test
    public void loginSuccess() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("abc")
                .password("abc")
                .build();
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authToken);
        when(jwtService.generateToken(any(String.class))).thenReturn("abcd");

        AuthResponse authResponse = authService.login(loginRequest);
        assertNotNull(authResponse);
        assert(authResponse.getToken().equals("abcd"));
        assert(authResponse.getMessage().equals("Login Successful"));
    }

    @Test
    public void loginFailure() {
        LoginRequest loginRequest = LoginRequest.builder()
                .username("abc")
                .password("abcd")
                .build();

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenThrow(new BadCredentialsException("Failure"));

        assertThrows(BadCredentialsException.class, () -> {
            authService.login(loginRequest);
        });
    }
}
