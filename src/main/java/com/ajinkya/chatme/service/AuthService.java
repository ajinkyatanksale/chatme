package com.ajinkya.chatme.service;

import com.ajinkya.chatme.dto.auth.AuthResponse;
import com.ajinkya.chatme.dto.auth.LoginRequest;
import com.ajinkya.chatme.dto.auth.RegistrationRequest;
import com.ajinkya.chatme.common.enums.UserStatus;
import com.ajinkya.chatme.entity.User;
import com.ajinkya.chatme.exception.UserAlreadyExistsException;
import com.ajinkya.chatme.repository.UserRepository;
import com.ajinkya.chatme.security.JwtService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.Optional;

@Slf4j
@Service
public class AuthService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtService jwtService;

    @Autowired
    AuthenticationManager authenticationManager;

    public AuthResponse register(RegistrationRequest registrationRequest) {

        Optional<User> existingUser = userRepository.findByUsername(registrationRequest.getUsername());

        if (existingUser.isPresent()) {
            throw new UserAlreadyExistsException("User already registered");
        }

        User user = User.builder()
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .username(registrationRequest.getUsername())
                .status(UserStatus.AWAY)
                .lastSeen(new Timestamp(System.currentTimeMillis()))
                .insertDt(new Timestamp(System.currentTimeMillis()))
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
}