package com.ajinkya.chatme.dto.room;

import com.ajinkya.chatme.dto.userInfo.User;
import lombok.*;

import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomResponse {
    private List<UUID> memberIds;
    private Timestamp createdAt;
    private String groupName;
    private String groupAvatarUrl;
    private User createdBy;
}
