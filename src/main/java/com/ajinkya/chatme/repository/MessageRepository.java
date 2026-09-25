package com.ajinkya.chatme.repository;

import com.ajinkya.chatme.entity.Message;
import com.ajinkya.chatme.entity.Room;
import com.ajinkya.chatme.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

import java.sql.Timestamp;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    Page<Message> findByRoomOrderByCreatedAtDesc(Room roomId, Pageable pageable);
    int countByRoomAndCreatedAtAfterAndUserNot(Room room, Timestamp lastReadAt, User user);
}
