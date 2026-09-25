package com.ajinkya.chatme.dto.error;

import lombok.*;
import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    Integer status;
    String error;
    String path;
    Instant timestamp;
}
