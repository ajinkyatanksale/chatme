package com.ajinkya.chatme.service;

import com.ajinkya.chatme.common.enums.ContentType;
import com.ajinkya.chatme.dto.message.MessageRequest;
import com.ajinkya.chatme.common.enums.MessageStatus;
import com.ajinkya.chatme.dto.message.Message;
import com.ajinkya.chatme.entity.Room;
import com.ajinkya.chatme.entity.RoomWithLastRead;
import com.ajinkya.chatme.entity.User;
import com.ajinkya.chatme.exception.ResourceNotFoundException;
import com.ajinkya.chatme.repository.MessageRepository;
import com.ajinkya.chatme.repository.RoomRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class MessageService {

    @Autowired
    MessageRepository messageRepository;

    @Autowired
    RoomRepository roomRepository;

    @Async("taskExecutor")
    public CompletableFuture<com.ajinkya.chatme.entity.Message> saveMessageAsync(MessageRequest messageRequest, User user, UUID roomId) {
        if (user == null) {
            return CompletableFuture.completedFuture(null);
        }

        List<RoomWithLastRead> rooms = roomRepository.findRoomsForUser(user.getId());
        RoomWithLastRead roomInfo = rooms.stream()
                .filter(room -> room.room().getId().equals(roomId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        com.ajinkya.chatme.entity.Message message = com.ajinkya.chatme.entity.Message.builder()
                .content(messageRequest.getContent())
                .room(roomInfo.room())
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .editedAt(new Timestamp(System.currentTimeMillis()))
                .messageStatus(MessageStatus.SENT)
                .contentType(ContentType.valueOf(messageRequest.getContentType()))
                .user(user)
                .build();

        com.ajinkya.chatme.entity.Message savedMessage = messageRepository.save(message);

        return CompletableFuture.completedFuture(savedMessage);
    }

    public List<Message> getMessageHistory(UUID roomId, int pageNumber, int pageSize, User user) {
        boolean isMember = validateRoomMembership(roomId, user);
        List<Message> messageList = new ArrayList<>();
        if (isMember) {
            Pageable pageable = PageRequest.of(pageNumber, pageSize);
            Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room not found:"));

            Page<com.ajinkya.chatme.entity.Message> messages = messageRepository.findByRoomOrderByCreatedAtDesc(room, pageable);

            com.ajinkya.chatme.dto.userInfo.User userDto = com.ajinkya.chatme.dto.userInfo.User.builder().email(user.getEmail())
                    .username(user.getUsername())
                    .status(user.getStatus().name())
                    .avatarUrl(user.getAvatarUrl())
                    .lastSeen(user.getLastSeen()).build();
            messages.stream().forEach(message -> {
                Message message1 = Message.builder().content(message.getContent())
                        .messageStatus(message.getMessageStatus().name())
                        .contentType(message.getContentType().name())
                        .editedAt(message.getEditedAt())
                        .createdAt(message.getCreatedAt())
                        .sender(userDto)
                        .roomId(message.getRoom().getId())
                        .build();
                messageList.add(message1);
            });

        }

        return messageList;
    }

    private boolean validateRoomMembership(UUID roomId, User user) {
        List<Room> rooms = roomRepository.findByCreatedBy(user);
        return rooms.stream().anyMatch(room -> room.getId().equals(roomId));
    }
}