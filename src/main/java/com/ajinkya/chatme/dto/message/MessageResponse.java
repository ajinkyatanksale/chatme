package com.ajinkya.chatme.dto.message;

import com.ajinkya.chatme.common.enums.ContentType;
import com.ajinkya.chatme.common.enums.MessageStatus;
import com.ajinkya.chatme.dto.userInfo.User;
import lombok.*;
import java.sql.Timestamp;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MessageResponse {
    private UUID id;
    private UUID roomId;
    private String content;
    private ContentType contentType;
    private User sender;
    private Timestamp createdAt;
    private MessageStatus status;
}
