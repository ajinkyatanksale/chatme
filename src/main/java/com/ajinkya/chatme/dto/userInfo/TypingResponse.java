package com.ajinkya.chatme.dto.userInfo;

import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypingResponse {
    private UUID userId;
    private String username;
    private Boolean isTyping;
}
