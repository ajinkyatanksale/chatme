package com.ajinkya.chatme.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name="room_member", schema = "room")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoomMember {
    @Id
    @Column(name = "room_member_id")
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    @Lazy
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
    @Lazy
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "role")
    private String role;
    @Column(name = "joined_at")
    private Timestamp joinedAt;
    @Column(name = "last_read_at")
    private Timestamp lastReadAt;
}
