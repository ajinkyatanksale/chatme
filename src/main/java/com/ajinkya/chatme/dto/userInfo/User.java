package com.ajinkya.chatme.dto.userInfo;

import lombok.*;
import java.sql.Timestamp;
import java.util.UUID;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {
    private UUID userId;
    private String email;
    private String avatarUrl;
    private String status;
    private Timestamp lastSeen;
    private String username;
}

