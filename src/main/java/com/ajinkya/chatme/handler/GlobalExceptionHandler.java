package com.ajinkya.chatme.handler;

import com.ajinkya.chatme.dto.auth.AuthResponse;
import com.ajinkya.chatme.dto.error.ErrorResponse;
import com.ajinkya.chatme.exception.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.nio.file.AccessDeniedException;
import java.time.Instant;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException re, HttpServletRequest request) {
        log.error("User or Room not found {}", re.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("User or Room not found")
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getPathInfo())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RoomMembershipException.class)
    public ResponseEntity<ErrorResponse> handleRoomMembershipException(RoomMembershipException e, HttpServletRequest request) {
        log.error("User not part of provided room {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("User or Room not found")
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getPathInfo())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequestException(BadRequestException be, HttpServletRequest request) {
        log.error("Invalid Request!! {}", be.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Invalid Request")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getPathInfo())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnauthorizedException ua, HttpServletRequest request) {
        log.error("User not authorized to access the resource {}", ua.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("User not authorized to access the resource")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request.getPathInfo())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException m, HttpServletRequest request) {
        log.error("Request validation failed {} -- {} -- {}", m.getMessage(), m.getBody(), m.getBindingResult().getFieldErrors());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Request validation failed")
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getPathInfo())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException a, HttpServletRequest request) {
        log.error("Access denied {}", a.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Access Denied")
                .status(HttpStatus.UNAUTHORIZED.value())
                .path(request.getPathInfo())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentialsException(BadCredentialsException e, HttpServletRequest request) {
        log.error("User not found {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("User not found")
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getPathInfo())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<AuthResponse> handleUserAlreadyExistsException(UserAlreadyExistsException e, HttpServletRequest request) {
        log.error(e.getMessage());
        AuthResponse authResponse = AuthResponse.builder()
                .message("Already registered")
                .build();
        return new ResponseEntity<>(authResponse, HttpStatus.IM_USED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
        log.error("Internal server error {}", e.getMessage());
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error("Internal server error")
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getPathInfo())
                .timestamp(Instant.now())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
