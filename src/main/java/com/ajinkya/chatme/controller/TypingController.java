package com.ajinkya.chatme.controller;

import com.ajinkya.chatme.dto.userInfo.TypingResponse;
import com.ajinkya.chatme.entity.User;
import com.ajinkya.chatme.service.TypingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import java.util.UUID;

@Controller
@Slf4j
public class TypingController {
    @Autowired
    TypingService typingService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @MessageMapping("/typing/{roomId}")
    public void sendTypingStatus(@DestinationVariable("roomId") UUID roomId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }
        log.info("RoomId {}", roomId);
        User user = (User) authentication.getPrincipal();
        if (user != null) {
            typingService.startTyping(user, roomId);
            String sendStr = "/topic/room/" + roomId + "/typing";
            log.info("Send Str {}", sendStr);
            TypingResponse typingResponse = TypingResponse.builder().isTyping(true)
                    .userId(user.getId())
                    .username(user.getUsername())
                    .build();
            simpMessagingTemplate.convertAndSend(sendStr, typingResponse);
        }
    }
}