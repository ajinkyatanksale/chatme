package com.ajinkya.chatme.service;

import com.ajinkya.chatme.common.enums.UserStatus;
import com.ajinkya.chatme.dto.userInfo.User;
import com.ajinkya.chatme.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class PresenceService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Autowired
    TaskExecutor taskExecutor;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionTemplate transactionTemplate;

    @Value("${spring.cache.timeout}")
    long timeOut;


    public void setOnline(com.ajinkya.chatme.entity.User user) {
        refreshPresence(user.getId());
        CompletableFuture.runAsync(() -> {
            transactionTemplate.executeWithoutResult(status -> {
                userRepository.updateStatusByUserId(UserStatus.ONLINE.name(), user.getId());
            });
        }, taskExecutor);
    }

    public void setOffline(com.ajinkya.chatme.entity.User user) {
        redisTemplate.delete("presence:" + user.getId());
        CompletableFuture.runAsync(() -> {
            transactionTemplate.executeWithoutResult(status -> {
                userRepository.updateStatusByUserId(UserStatus.OFFLINE.name(), user.getId());
            });
        }, taskExecutor);
    }

    public boolean isOnline(com.ajinkya.chatme.entity.User user) {
        String value = redisTemplate.opsForValue().get("presence:" + user.getId());
        if (value == null) {
            return user.getStatus() == UserStatus.ONLINE;
        }
        return false;
    }

    public List<User> getOnlineUsersInRoom(UUID roomId) {
        List<com.ajinkya.chatme.entity.User> onlineUsers = userRepository.getByRoomAndUserStatus(roomId, UserStatus.ONLINE.name());
        List<User> userList = new ArrayList<>();

        onlineUsers.forEach(user -> {
            User user1 = User.builder()
                    .email(user.getEmail())
                    .lastSeen(user.getLastSeen())
                    .avatarUrl(user.getAvatarUrl())
                    .status(user.getStatus().name())
                    .username(user.getUsername())
                    .build();
            userList.add(user1);
        });

        return userList;
    }

    public void refreshPresence(UUID userId) {
        redisTemplate.opsForValue().set("presence:" + userId, UserStatus.ONLINE.name(), Duration.ofSeconds(timeOut));
        log.info("SetOnline: {}", redisTemplate.opsForValue().get("presence:" + userId));
    }
}

