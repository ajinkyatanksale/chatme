package com.ajinkya.chatme.repository;

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
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Transactional
    @Modifying
    @Query(value = "update auth.user set status = :user_status where user_id = :user_id", nativeQuery = true)
    int updateStatusByUserId(@Param("user_status") String userStatus, @Param("user_id") UUID userId);

    @Query(value = "select * from auth.user u, room.room r where r.user_id=u.user_id and r.room_id=:room_id and u.status=:status", nativeQuery = true)
    List<User> getByRoomAndUserStatus(@Param("room_id") UUID roomId, @Param("status") String status);
}
