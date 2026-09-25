package com.ajinkya.chatme.dto.room;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {
    @NotNull
    @Size(min = 5, max = 20)
    private String name;
    @NotNull
    private String RoomType;
}
