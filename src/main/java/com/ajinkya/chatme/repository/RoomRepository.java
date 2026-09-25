package com.ajinkya.chatme.repository;

import com.ajinkya.chatme.entity.Room;
import com.ajinkya.chatme.entity.RoomWithLastRead;
import com.ajinkya.chatme.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    List<Room> findByCreatedBy(User user);

    @Query(value = """
        SELECT new com.ajinkya.chatme.entity.RoomWithLastRead(rm.room, rm.lastReadAt)
        FROM RoomMember rm
        WHERE rm.user.id = :userId
        """)
    List<RoomWithLastRead> findRoomsForUser(@Param("userId") UUID userId);
}
