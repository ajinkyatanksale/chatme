package com.ajinkya.chatme.dto.room;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRoomRequest {
    @NotEmpty
    private List<UUID> members;
    @NotNull
    @Size(min = 5, max = 20)
    private String groupName;
    @NotNull
    private String groupAvatarUrl;
}
