package com.ajinkya.chatme.dto.room;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class JoinRoomResponse {
    private String message;
    private String failureMessage;
}
