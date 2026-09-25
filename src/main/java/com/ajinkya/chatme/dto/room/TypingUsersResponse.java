package com.ajinkya.chatme.dto.room;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TypingUsersResponse {
    private List<UUID> userId;
    private String failureMessage;
}
