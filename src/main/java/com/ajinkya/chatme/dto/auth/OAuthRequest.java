package com.ajinkya.chatme.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OAuthRequest {
    @NotBlank
    private String token;
}
