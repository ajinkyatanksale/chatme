package com.ajinkya.chatme.service;

import com.ajinkya.chatme.common.enums.RoomType;
import com.ajinkya.chatme.dto.room.RoomInfo;
import com.ajinkya.chatme.entity.Room;
import com.ajinkya.chatme.entity.RoomMember;
import com.ajinkya.chatme.entity.RoomWithLastRead;
import com.ajinkya.chatme.entity.User;
import com.ajinkya.chatme.repository.MessageRepository;
import com.ajinkya.chatme.repository.RoomMemberRepository;
import com.ajinkya.chatme.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class RoomService {

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    RoomMemberRepository roomMemberRepository;

    @Autowired
    MessageRepository messageRepository;

    public boolean createRoom(String name, RoomType roomType, User user) {
        Room room = Room.builder()
                .name(name)
                .roomType(roomType)
                .createdBy(user)
                .createdAt(new Timestamp(System.currentTimeMillis()))
                .build();
        room = roomRepository.save(room);
        RoomMember roomMember = RoomMember.builder()
                .room(room)
                .role("ADMIN")
                .lastReadAt(new Timestamp(System.currentTimeMillis()))
                .user(user)
                .joinedAt(new Timestamp(System.currentTimeMillis()))
                .build();
        roomMember = roomMemberRepository.save(roomMember);
        return roomMember.getId() != null && room.getId() != null;
    }

    public boolean joinRoom(UUID roomId, User user) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));
        RoomMember roomMember = RoomMember.builder()
                .room(room)
                .role("MEMBER")
                .lastReadAt(new Timestamp(System.currentTimeMillis()))
                .user(user)
                .joinedAt(new Timestamp(System.currentTimeMillis()))
                .build();
        roomMember = roomMemberRepository.save(roomMember);
        return roomMember.getId() != null;
    }

    public List<RoomInfo> getRoomsCreatedByUser(User user) {
        List<Room> listOfRooms = roomRepository.findByCreatedBy(user);
        List<RoomInfo> roomInfoList = new ArrayList<>();
        listOfRooms.forEach(room -> {
            RoomInfo roomInfo = RoomInfo.builder()
                    .roomId(room.getId())
                    .roomType(room.getRoomType().name())
                    .name(room.getName())
                    .createdBy(room.getCreatedBy().getUsername())
                    .createdAt(room.getCreatedAt())
                    .unReadCount(0)
                    .build();
            roomInfoList.add(roomInfo);
        });
        return roomInfoList;
    }

    public List<RoomInfo> getAllRoomsForUser(User user) {
        List<RoomWithLastRead> listOfRooms = roomRepository.findRoomsForUser(user.getId());
        List<RoomInfo> roomInfoList = new ArrayList<>();
        listOfRooms.forEach(room -> {
            RoomInfo.RoomInfoBuilder roomInfoBuilder = RoomInfo.builder()
                    .roomId(room.room().getId())
                    .roomType(room.room().getRoomType().name())
                    .name(room.room().getName())
                    .createdBy(room.room().getCreatedBy().getUsername())
                    .createdAt(room.room().getCreatedAt());

            int count = messageRepository.countByRoomAndCreatedAtAfterAndUserNot(room.room(), room.lastReadAt(), user);
            roomInfoList.add(roomInfoBuilder.unReadCount(count).build());
        });
        return roomInfoList;
    }
}
