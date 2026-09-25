package com.ajinkya.chatme.dto.message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class MessageRequest {
    @NotBlank
    @Size(max=5000)
    private String content;
    @NotBlank
    private String contentType;
}
