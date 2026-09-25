package com.ajinkya.chatme.service;

import com.ajinkya.chatme.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TypingService {

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    @Value("${spring.cache.timeout}")
    long timeOut;

    @Async
    public void startTyping(User user, UUID roomId) {
        redisTemplate.opsForValue().set("typing:" + roomId + ":" + user.getId(), "TRUE", Duration.ofSeconds(timeOut));
        log.info("Typing Status {} {}", "typing:" + roomId + ":" + user.getId(), redisTemplate.opsForValue().get("typing:" + roomId + ":" + user.getId()));
    }

    public List<UUID> getTypingUsers(UUID roomId) {
        String keyPattern = "typing:" + roomId + ":*";
        String prefix = "typing:" + roomId + ":";
        List<UUID> userIds = new ArrayList<>();

        ScanOptions options = ScanOptions.scanOptions()
                .match(keyPattern)
                .count(100)
                .build();

        try (Cursor<String> cursor = redisTemplate.scan(options)) {
            while (cursor.hasNext()) {
                String key = cursor.next();
                if (key.startsWith(prefix)) {
                    String userId = key.substring(prefix.length());
                    userIds.add(UUID.fromString(userId));
                }
            }
        }
        return userIds;
    }
}