package com.ajinkya.chatme.controller;

import com.ajinkya.chatme.dto.message.MessageRequest;
import com.ajinkya.chatme.dto.message.MessageResponse;
import com.ajinkya.chatme.entity.User;
import com.ajinkya.chatme.service.MessageService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@Controller
@Slf4j
public class ChatController {
    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;
    @Autowired
    MessageService messageService;

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@DestinationVariable("roomId") UUID roomId, @RequestBody @Valid MessageRequest messageRequest, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            return;
        }
        User user = (User) authentication.getPrincipal();

        messageService.saveMessageAsync(messageRequest, user, roomId)
                .thenAccept(savedMessage -> {
                    if (savedMessage != null) {
                        User userEntity = savedMessage.getUser();
                        com.ajinkya.chatme.dto.userInfo.User userDto = com.ajinkya.chatme.dto.userInfo.User.builder()
                                .userId(userEntity.getId())
                                .email(userEntity.getEmail())
                                        .username(userEntity.getUsername())
                                                .status(userEntity.getStatus().name())
                                                        .avatarUrl(userEntity.getAvatarUrl())
                                                                .lastSeen(userEntity.getLastSeen()).build();
                        MessageResponse messageResponse = MessageResponse.builder()
                                .id(savedMessage.getId())
                                        .createdAt(savedMessage.getCreatedAt())
                                                .status(savedMessage.getMessageStatus())
                                                        .content(savedMessage.getContent())
                                                                .contentType(savedMessage.getContentType())
                                                                        .roomId(roomId)
                                                                                .sender(userDto).build();
                        String sendStr = String.format("/topic/room/%s", roomId);
                        simpMessagingTemplate.convertAndSend(sendStr, messageResponse);
                    }
                })
                .exceptionally(ex -> {
                    log.error("Exception occurred {}", ex.getMessage());
                    if (user != null) {
                        simpMessagingTemplate.convertAndSendToUser(
                                user.getUsername(),
                                "/queue/errors",
                                "Internal Server Error"
                        );
                    }
                    return null;
                });
    }
}
