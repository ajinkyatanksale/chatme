package com.ajinkya.chatme.dto.room;

import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoomInfo {
    private UUID roomId;
    private String name;
    private String roomType;
    private String createdBy;
    private Timestamp createdAt;
    private int unReadCount;
}
