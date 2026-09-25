package com.ajinkya.chatme.dto.message;

import lombok.*;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MessageHistoryResponse {
    private List<Message> messages;
    private String failureMessage;
}
