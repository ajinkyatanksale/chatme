package com.ajinkya.chatme.dto.error;

import lombok.*;
import java.time.Instant;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {
    private Integer status;
    private String error;
    private String path;
    private Instant timestamp;
}
