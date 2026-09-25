package com.ajinkya.chatme.repository;

import com.ajinkya.chatme.entity.Room;
import com.ajinkya.chatme.entity.RoomMember;
import com.ajinkya.chatme.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoomMemberRepository extends JpaRepository<RoomMember, UUID> {
    Optional<RoomMember> findByRoomAndUser(Room room, User user);

    @Query(value = "select * from room.room r, room.room_member rm where rm.room_id=r.room_id and rm.user_id = :user_id", nativeQuery = true)
    Optional<Room> findAllRoomByUserId(@Param("user_id") UUID user_id);

    @Query(value = "select * from room.room_member rm where rm.room_id=:room_id and rm.user_id <> :sender_id", nativeQuery = true)
    List<RoomMember> findRecipientIdsByRoomId(@Param("room_id") UUID roomId, @Param("sender_id") UUID senderId);

    @Transactional
    @Modifying
    @Query(value = "update room.room_member rm set rm.last_read_at = NOW() where rm.room_id=:room_id and rm.user_id=:user_id", nativeQuery = true)
    int markAsRead(@Param("room_id") UUID roomId, @Param("user_id") UUID userId);
}

