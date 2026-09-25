package com.ajinkya.chatme.dto.message;

import com.ajinkya.chatme.dto.userInfo.User;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.sql.Timestamp;
import java.util.UUID;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Message {
    private UUID roomId;
    private User sender;
    private String content;
    private String contentType;
    private String messageStatus;
    private Timestamp createdAt;
    private Timestamp editedAt;
}
