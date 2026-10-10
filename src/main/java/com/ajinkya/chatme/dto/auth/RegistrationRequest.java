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
    @NotBlank @Size(min=8, max=20)
    private String password;
    @NotBlank
    private String name;
    @Email
    private String email;
    @NotBlank @Size(min = 10, max = 10)
    private String mobileNumber;
}
