package com.ajinkya.chatme.service;

import com.ajinkya.chatme.dto.auth.AuthResponse;
import com.ajinkya.chatme.dto.auth.LoginRequest;
import com.ajinkya.chatme.dto.auth.RegistrationRequest;
import com.ajinkya.chatme.common.enums.UserStatus;
import com.ajinkya.chatme.entity.User;
import com.ajinkya.chatme.exception.UserAlreadyExistsException;
import com.ajinkya.chatme.repository.UserRepository;
import com.ajinkya.chatme.security.JwtService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final GoogleIdTokenVerifier googleIdTokenVerifier;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtService jwtService, AuthenticationManager authenticationManager,
                       @Value("${spring.app.google.client-id}") String clientId) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.googleIdTokenVerifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }

    public AuthResponse register(RegistrationRequest registrationRequest) {

        Optional<User> existingUser = userRepository.findByUsername(registrationRequest.getEmail());

        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User already registered");
        }

        User user = User.builder()
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .username(registrationRequest.getEmail())
                .status(UserStatus.AWAY)
                .lastSeen(new Timestamp(System.currentTimeMillis()))
                .insertDt(new Timestamp(System.currentTimeMillis()))
                .mobileNumber(registrationRequest.getMobileNumber())
                .name(registrationRequest.getName())
                .build();
        userRepository.save(user);

        String token = jwtService.generateToken(user.getUsername());

        return AuthResponse.builder()
                .message("Registration Successful")
                .token(token)
                .build();
    }

    public AuthResponse login(LoginRequest loginRequest) throws BadCredentialsException {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()));
        String username;
        if (authentication.getPrincipal() instanceof User) {
            username = ((User) authentication.getPrincipal()).getUsername();
        } else {
            username = (String) authentication.getPrincipal();
        }
        if (username != null && !username.isBlank()) {
            String token = jwtService.generateToken(username);
            return AuthResponse.builder()
                    .message("Login Successful")
                    .token(token)
                    .build();
        } else {
            throw new BadCredentialsException("Password Validation Failed");
        }
    }

    public AuthResponse oAuthVerification(String idTokenString) {
        try {
            GoogleIdToken idToken = googleIdTokenVerifier.verify(idTokenString);

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String name = (String) payload.get("name");
                String pictureUrl = (String) payload.get("picture");

                User user = userRepository.findByEmail(email).orElseGet(() -> {
                    User newUser = User.builder()
                            .email(email)
                            .password(UUID.randomUUID().toString().substring(0, 10))
                            .username(email)
                            .status(UserStatus.AWAY)
                            .insertDt(new Timestamp(System.currentTimeMillis()))
                            .lastSeen(new Timestamp(System.currentTimeMillis()))
                            .mobileNumber("0000000000")
                            .name(name)
                            .avatarUrl(pictureUrl)
                            .build();

                    return userRepository.save(newUser);
                });

                String token = jwtService.generateToken(user.getUsername());
                return AuthResponse.builder()
                        .message("Login Successful")
                        .token(token)
                        .build();
            } else {
                throw new BadCredentialsException("Invalid Google ID token.");
            }
        } catch (Exception e) {
            throw new BadCredentialsException("Token verification failed: " + e.getMessage());
        }
    }
}