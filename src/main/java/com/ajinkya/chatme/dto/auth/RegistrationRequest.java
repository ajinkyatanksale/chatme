package com.ajinkya.chatme.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest {
    @NotBlank @Size(min=3, max=50)
    private String username;
    @NotBlank @Size(min=8, max=20)
    private String password;
    @NotBlank
    private String name;
    @Email
    private String email;
}
