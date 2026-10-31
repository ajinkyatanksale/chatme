package com.ajinkya.chatme.dto.room;

import com.ajinkya.chatme.dto.message.Message;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateDMRequest {
    @NotNull
    private UUID receiverUserId;
    @NotNull
    private Message message;
}
