package com.ajinkya.chatme.service;

import com.ajinkya.chatme.entity.RoomMember;
import com.ajinkya.chatme.repository.RoomMemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

// Redis based implementation for unread messages. Will not be used as this is unreliable
@Service
public class UnreadService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    RoomMemberRepository roomMemberRepository;

    @Async
    public void incrementUnread(UUID roomId, UUID senderId) {
        List<RoomMember> roomMembersList = roomMemberRepository.findRecipientIdsByRoomId(roomId, senderId);
        roomMembersList.forEach(roomMember -> {
           UUID memberUserId = roomMember.getUser().getId();
               redisTemplate.opsForValue().increment("unread:" + memberUserId + ":" + roomId);
        });

    }

    public void resetUnread(UUID roomId, UUID userId) {
        redisTemplate.opsForValue().getAndDelete("unread:" + userId + ":" + roomId);
        roomMemberRepository.markAsRead(roomId, userId);
    }

    public int unreadCount(UUID roomId, UUID userId) {
        String unreadCount = redisTemplate.opsForValue().get("unread:" + userId + ":" + roomId);
        if (unreadCount != null && !unreadCount.isBlank()) {
            return Integer.parseInt(unreadCount);
        }
        return 0;
    }
}
