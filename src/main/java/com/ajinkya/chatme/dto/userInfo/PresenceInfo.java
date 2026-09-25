package com.ajinkya.chatme.dto.userInfo;

import lombok.*;

import java.util.UUID;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PresenceInfo {
    private UUID userId;
    private String status;
}
