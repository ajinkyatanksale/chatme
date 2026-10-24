package com.ajinkya.chatme.controller;

import com.ajinkya.chatme.dto.userInfo.FindUserResponse;
import com.ajinkya.chatme.entity.User;
import com.ajinkya.chatme.exception.UnauthorizedException;
import com.ajinkya.chatme.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/chat-me/user")
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/getbyuserinfo")
    public ResponseEntity<FindUserResponse> getUserByUsername(SecurityContext securityContext) {
        Authentication authentication = securityContext.getAuthentication();
        com.ajinkya.chatme.dto.userInfo.FindUserResponse findUserResponse;
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            if (user != null) {
                com.ajinkya.chatme.dto.userInfo.User user1 = userService.getUserByUsername(user.getUsername());
                return new ResponseEntity<>(FindUserResponse.builder().user(user1).build(), HttpStatus.OK);
            } else {
                throw new UnauthorizedException("User not authorized!");
            }
        } else {
            throw new UnauthorizedException("User not authorized!");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<List<com.ajinkya.chatme.dto.userInfo.User>> searchByPrefix(@RequestParam("prefix") @Valid @NotBlank @Size(min = 3) String prefix, SecurityContext securityContext) {
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            if (user != null) {
                return new ResponseEntity<>(userService.getDynamicUsersByUsernamePrefix(prefix, user.getUsername()), HttpStatus.OK);
            } else {
                throw new UnauthorizedException("User not authorized!");
            }
        } else {
            throw new UnauthorizedException("User not authorized!");
        }
    }
}
