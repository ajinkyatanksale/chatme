package com.ajinkya.chatme.service;

import com.ajinkya.chatme.common.enums.ContentType;
import com.ajinkya.chatme.common.enums.MessageStatus;
import com.ajinkya.chatme.common.enums.RoomType;
import com.ajinkya.chatme.dto.message.MessageRequest;
import com.ajinkya.chatme.entity.Message;
import com.ajinkya.chatme.entity.Room;
import com.ajinkya.chatme.entity.RoomWithLastRead;
import com.ajinkya.chatme.entity.User;
import com.ajinkya.chatme.repository.MessageRepository;
import com.ajinkya.chatme.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@ExtendWith(MockitoExtension.class)
public class MessageServiceTest {

    @InjectMocks
    MessageService messageService;

    @Mock
    MessageRepository messageRepository;

    @Mock
    RoomRepository roomRepository;

    @Test
    public void saveMessageSuccess() {
        MessageRequest messageRequest = MessageRequest.builder()
                        .contentType(ContentType.TEXT.name())
                                .content("Hello")
                                        .build();
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("")
                .build();
        Room room = Room.builder()
                .id(UUID.randomUUID())
                .roomType(RoomType.DM)
                .name("Hi")
                .build();

        com.ajinkya.chatme.entity.Message message = com.ajinkya.chatme.entity.Message.builder()
                .content(messageRequest.getContent())
                .room(room)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .editedAt(new Timestamp(System.currentTimeMillis()))
                .messageStatus(MessageStatus.SENT)
                .contentType(ContentType.valueOf(messageRequest.getContentType()))
                .user(user)
                .build();

        RoomWithLastRead roomWithLastRead = new RoomWithLastRead(room, new Timestamp(System.currentTimeMillis()));
        List<RoomWithLastRead> rooms = List.of(roomWithLastRead);
        when(roomRepository.findRoomsForUser(any(UUID.class))).thenReturn(rooms);
        when(messageRepository.save(any(Message.class))).thenReturn(message);

        CompletableFuture<Message> savedMessageAsync = messageService.saveMessageAsync(messageRequest, user, room.getId());

        assert savedMessageAsync != null;
        assert savedMessageAsync.isDone();
    }

    @Test
    public void saveMessageFailure() {
        MessageRequest messageRequest = MessageRequest.builder()
                .contentType(ContentType.TEXT.name())
                .content("Hello")
                .build();
        User user = User.builder()
                .id(UUID.randomUUID())
                .username("")
                .build();
        Room room = Room.builder()
                .id(UUID.randomUUID())
                .roomType(RoomType.DM)
                .name("Hi")
                .build();

        RoomWithLastRead roomWithLastRead = new RoomWithLastRead(room, new Timestamp(System.currentTimeMillis()));
        List<RoomWithLastRead> rooms = List.of(roomWithLastRead);
        when(roomRepository.findRoomsForUser(any(UUID.class))).thenReturn(rooms);

        assertThrows(IllegalArgumentException.class, () -> {
            messageService.saveMessageAsync(messageRequest, user, UUID.randomUUID());
        });
    }
}
