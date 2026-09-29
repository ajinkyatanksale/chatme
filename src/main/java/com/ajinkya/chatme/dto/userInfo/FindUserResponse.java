package com.ajinkya.chatme.dto.userInfo;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FindUserResponse {
    private User user;
    private String failureMessage;
}
