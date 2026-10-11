package com.ajinkya.chatme.controller;

import com.ajinkya.chatme.dto.room.*;
import com.ajinkya.chatme.dto.message.MessageHistoryResponse;
import com.ajinkya.chatme.dto.message.Message;
import com.ajinkya.chatme.common.enums.RoomType;
import com.ajinkya.chatme.entity.User;
import com.ajinkya.chatme.exception.ResourceNotFoundException;
import com.ajinkya.chatme.exception.UnauthorizedException;
import com.ajinkya.chatme.service.MessageService;
import com.ajinkya.chatme.service.PresenceService;
import com.ajinkya.chatme.service.RoomService;
import com.ajinkya.chatme.service.TypingService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
@RequestMapping("/chat-me/room")
public class RoomController {

    @Autowired
    RoomService roomService;

    @Autowired
    MessageService messageService;

    @Autowired
    TypingService typingService;

    @Autowired
    PresenceService presenceService;

    @PostMapping("/")
    public ResponseEntity<String> createRoom(@RequestBody @Valid CreateRoomRequest createRoomRequest, SecurityContext securityContext) {
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            if (user != null) {
                if (roomService.createRoom(createRoomRequest.getName(), RoomType.valueOf(createRoomRequest.getRoomType()), user) != null) {
                    return new ResponseEntity<>("Room created successfully!", HttpStatus.OK);                    
                } else {
                    throw new ResourceNotFoundException("Room creation Failed!");
                }
            } else {
                throw new UnauthorizedException("User not authorized!");
            }
        } else {
            throw new UnauthorizedException("User not authorized!");
        }
    }

    @GetMapping("/")
    public ResponseEntity<RoomResponse> myCreatedRooms(SecurityContext securityContext) {
        Authentication authentication = securityContext.getAuthentication();
        RoomResponse roomResponse;
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            if (user != null) {
                List<RoomInfo> room = roomService.getRoomsCreatedByUser(user);
                roomResponse = RoomResponse.builder().rooms(room).build();
                return new ResponseEntity<>(roomResponse, HttpStatus.OK);
            } else {
                throw new UnauthorizedException("User not authorized!");
            }
        } else {
            throw new UnauthorizedException("User not authorized!");
        }
    }

    @GetMapping("/rooms")
    public ResponseEntity<RoomResponse> myAllRooms(SecurityContext securityContext) {
        Authentication authentication = securityContext.getAuthentication();
        RoomResponse roomResponse;
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            if (user != null) {
                List<RoomInfo> room = roomService.getAllRoomsForUser(user);
                roomResponse = RoomResponse.builder().rooms(room).build();
                return new ResponseEntity<>(roomResponse, HttpStatus.OK);
            } else {
                throw new UnauthorizedException("User not authorized!");
            }
        } else {
            throw new UnauthorizedException("User not authorized!");
        }
    }

    @GetMapping("/{id}/message")
    public ResponseEntity<MessageHistoryResponse> history(@PathVariable("id") UUID roomId, @RequestParam("pagenumber") int pageNumber, @RequestParam("pagesize") int pageSize, SecurityContext securityContext) {
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            if (user == null) {
                throw new UnauthorizedException("User not authorized!");
            } else {
                List<Message> messages = messageService.getMessageHistory(roomId, pageNumber, pageSize, user);
                if (messages != null) {
                    MessageHistoryResponse messageHistoryResponse = MessageHistoryResponse.builder().messages(messages).build();
                    return new ResponseEntity<>(messageHistoryResponse, HttpStatus.OK);
                } else {
                    throw new ResourceNotFoundException("User not enrolled in this room");
                }
            }
        } else {
            throw new ResourceNotFoundException("User not enrolled in this room");
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<JoinRoomResponse> joinRoom(@PathVariable("id") UUID roomId, SecurityContext securityContext) {
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            if (user == null) {
                throw new UnauthorizedException("User not authorized!");
            } else {
                boolean isRoomJoinSuccessful = roomService.joinRoom(roomId, user);
                if (isRoomJoinSuccessful) {
                    JoinRoomResponse joinRoomResponse = JoinRoomResponse.builder().message("Room joining successful").build();
                    return new ResponseEntity<>(joinRoomResponse, HttpStatus.OK);
                } else {
                    throw new ResourceNotFoundException("Room join operation failed");
                }
            }
        } else {
            throw new UnauthorizedException("User not authorized!");
        }
    }

    @GetMapping("/{id}/typing")
    public ResponseEntity<TypingUsersResponse> getTypingUsers(@PathVariable("id") UUID roomId, SecurityContext securityContext) {
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            if (user == null) {
                throw new UnauthorizedException("User not authorized!");
            } else {
                List<UUID> users = typingService.getTypingUsers(roomId);
                if (users != null) {
                    TypingUsersResponse typingUsersResponse = TypingUsersResponse.builder()
                            .userId(users)
                            .build();
                    return new ResponseEntity<>(typingUsersResponse, HttpStatus.OK);
                } else {
                    throw new ResourceNotFoundException("User not enrolled in this room");
                }
            }
        } else {
            throw new ResourceNotFoundException("User not enrolled in this room");
        }
    }

    @GetMapping("/{id}/online")
    public ResponseEntity<OnlineUsersResponse> getOnlineUsers(@PathVariable("id") UUID roomId, SecurityContext securityContext) {
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            if (user == null) {
                throw new UnauthorizedException("User not authorized!");
            } else {
                List<com.ajinkya.chatme.dto.userInfo.User> users = presenceService.getOnlineUsersInRoom(roomId);
                if (users != null) {
                    return new ResponseEntity<>(OnlineUsersResponse.builder().onlineUsers(users).build(), HttpStatus.OK);
                } else {
                    throw new ResourceNotFoundException("User not enrolled in this room");
                }
            }
        } else {
            throw new ResourceNotFoundException("User not enrolled in this room");
        }
    }

    @PostMapping("/dm/{id}")
    public ResponseEntity<JoinRoomResponse> createDM(@PathVariable("id") UUID userId, SecurityContext securityContext) {
        Authentication authentication = securityContext.getAuthentication();
        if (authentication != null) {
            User user = (User) authentication.getPrincipal();
            if (user == null) {
                throw new UnauthorizedException("User not authorized!");
            } else {
                String message = roomService.findUserAndCreateDM(user, userId);
                JoinRoomResponse joinRoomResponse = JoinRoomResponse.builder().message(message).build();
                return new ResponseEntity<>(joinRoomResponse, HttpStatus.OK);

            }
        } else {
            throw new UnauthorizedException("User not authorized!");
        }
    }
}