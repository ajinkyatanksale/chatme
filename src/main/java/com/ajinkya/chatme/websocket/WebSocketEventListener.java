package com.ajinkya.chatme.websocket;

import com.ajinkya.chatme.common.enums.UserStatus;
import com.ajinkya.chatme.dto.userInfo.PresenceInfo;
import com.ajinkya.chatme.entity.RoomWithLastRead;
import com.ajinkya.chatme.entity.User;
import com.ajinkya.chatme.repository.RoomRepository;
import com.ajinkya.chatme.service.PresenceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import java.util.List;

@Component
@Slf4j
public class WebSocketEventListener {

    @Autowired
    PresenceService presenceService;

    @Autowired
    SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    RoomRepository roomRepository;

    @EventListener(SessionConnectedEvent.class)
    public void setOnlineStatus(SessionConnectedEvent event) {
        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) StompHeaderAccessor.wrap(event.getMessage()).getUser();
        if (user != null) {
            User user1 = (User) user.getPrincipal();
            if (user1 != null) {
                presenceService.setOnline(user1);
                PresenceInfo presenceInfo = PresenceInfo.builder().userId(user1.getId())
                        .status(UserStatus.ONLINE.name()).build();
                List<RoomWithLastRead> rooms = roomRepository.findRoomsForUser(user1.getId());
                rooms.forEach(room -> simpMessagingTemplate.convertAndSend("/topic/room/status/" + room.room().getId(), presenceInfo));
            }
        }
    }

    @EventListener(SessionDisconnectEvent.class)
    public void setOfflineStatus(SessionDisconnectEvent event) {
        UsernamePasswordAuthenticationToken user = (UsernamePasswordAuthenticationToken) StompHeaderAccessor.wrap(event.getMessage()).getUser();
        if (user != null) {
            User user1 = (User) user.getPrincipal();
            if (user1 != null) {
                presenceService.setOffline(user1);
                PresenceInfo presenceInfo = PresenceInfo.builder().userId(user1.getId())
                        .status(UserStatus.OFFLINE.name()).build();
                List<RoomWithLastRead> rooms = roomRepository.findRoomsForUser(user1.getId());

                rooms.forEach(room -> simpMessagingTemplate.convertAndSend("/topic/room/status/" + room.room().getId(), presenceInfo));
            }
        }
    }
}
