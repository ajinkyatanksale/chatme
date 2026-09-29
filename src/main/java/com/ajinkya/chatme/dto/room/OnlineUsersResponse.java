package com.ajinkya.chatme.dto.room;

import com.ajinkya.chatme.dto.userInfo.User;
import lombok.*;
import java.util.List;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OnlineUsersResponse {
    private List<User> onlineUsers;
    private String failureMessage;
}
