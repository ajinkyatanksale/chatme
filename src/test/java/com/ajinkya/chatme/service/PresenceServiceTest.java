package com.ajinkya.chatme.service;

import com.ajinkya.chatme.common.enums.UserStatus;
import com.ajinkya.chatme.entity.User;
import com.ajinkya.chatme.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionTemplate;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.UUID;
import java.util.function.Consumer;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class PresenceServiceTest {

    @Mock
    private RedisTemplate<String, String> redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionTemplate transactionTemplate;

    @InjectMocks
    PresenceService presenceService;

    private UUID testUserId;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUserId = UUID.randomUUID();
        testUser = User.builder()
                .id(testUserId)
                .username("john_doe")
                .email("john@example.com")
                .status(UserStatus.ONLINE)
                .avatarUrl(null)
                .lastSeen(new Timestamp(System.currentTimeMillis()))
                .build();

        ReflectionTestUtils.setField(presenceService, "timeOut", 30);
        ReflectionTestUtils.setField(presenceService, "taskExecutor", new SyncTaskExecutor());

        lenient().doAnswer(invocation -> {
            Consumer<Object> action = invocation.getArgument(0);
            action.accept(null);
            return null;
        }).when(transactionTemplate).executeWithoutResult(any());

        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    @DisplayName("Should refresh redis presence and update user status in repository asynchronously")
    void shouldSetOnlineSuccessfully() {
        presenceService.setOnline(testUser);

        verify(valueOperations).set(
                eq("presence:" + testUserId),
                eq(UserStatus.ONLINE.name()),
                eq(Duration.ofSeconds(30))
        );

        verify(userRepository).updateStatusByUserId(UserStatus.ONLINE.name(), testUserId);
    }

    @Test
    @DisplayName("Should delete redis presence key and update user status to OFFLINE in repository")
    void shouldSetOfflineSuccessfully() {
        presenceService.setOffline(testUser);
        verify(redisTemplate).delete("presence:" + testUserId);
        verify(userRepository).updateStatusByUserId(UserStatus.OFFLINE.name(), testUserId);
    }

    @Test
    @DisplayName("Should set presence key in Redis with configured timeout duration")
    void shouldSetPresenceWithTimeout() {
        presenceService.refreshPresence(testUserId);

        verify(valueOperations).set(
                eq("presence:" + testUserId),
                eq(UserStatus.ONLINE.name()),
                eq(Duration.ofSeconds(30))
        );
        verify(valueOperations).get("presence:" + testUserId);
    }
}
