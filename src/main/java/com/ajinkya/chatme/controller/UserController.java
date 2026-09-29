package com.ajinkya.chatme.controller;

import com.ajinkya.chatme.dto.userInfo.FindUserResponse;
import com.ajinkya.chatme.entity.User;
import com.ajinkya.chatme.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/chat-me/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/getbyusername")
    public ResponseEntity<FindUserResponse> getUserByUsername(@RequestParam("username") @Valid @NotBlank String username, SecurityContext securityContext) {
        Authentication authentication = securityContext.getAuthentication();
        com.ajinkya.chatme.dto.userInfo.FindUserResponse findUserResponse;
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            if (user != null) {
                com.ajinkya.chatme.dto.userInfo.User user1 = userService.getUserByUsername(username);
                return new ResponseEntity<>(FindUserResponse.builder().user(user1).build(), HttpStatus.OK);
            } else {
                findUserResponse = FindUserResponse.builder().failureMessage("User not authorized!").build();
                return new ResponseEntity<>(findUserResponse, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            findUserResponse = FindUserResponse.builder().failureMessage("User not authorized!").build();
            return new ResponseEntity<>(findUserResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
