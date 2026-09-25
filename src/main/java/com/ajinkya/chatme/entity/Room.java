package com.ajinkya.chatme.entity;

import com.ajinkya.chatme.common.enums.RoomType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "room", schema = "room")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Room {

    @Id
    @Column(name = "room_id")
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    @Column(name = "name")
    private String name;
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private RoomType roomType;
    @Lazy
    @JoinColumn(name = "created_by")
    @ManyToOne
    private User createdBy;
    @Column(name = "created_at")
    private Timestamp createdAt;
}
