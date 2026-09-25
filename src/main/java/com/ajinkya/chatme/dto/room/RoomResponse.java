package com.ajinkya.chatme.dto.room;
import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class RoomResponse {
    private List<RoomInfo> rooms;
    private String failureMessage;
}
