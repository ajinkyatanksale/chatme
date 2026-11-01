package com.ajinkya.chatme.dto.userInfo;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetUserHandleRequest {
    @NotNull
    private String userHandle;
}
