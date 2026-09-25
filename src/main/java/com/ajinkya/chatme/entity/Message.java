package com.ajinkya.chatme.entity;

import com.ajinkya.chatme.common.enums.ContentType;
import com.ajinkya.chatme.common.enums.MessageStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "message", schema = "message")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Message {
    @Id
    @Column(name = "message_id")
    @GeneratedValue(strategy= GenerationType.UUID)
    private UUID id;
    @ManyToOne
    @Lazy
    @JoinColumn(name = "room_id")
    private Room room;
    @ManyToOne
    @Lazy
    @JoinColumn(name = "sender_id")
    private User user;
    @Column(name = "content")
    private String content;
    @Column(name = "content_type")
    @Enumerated(EnumType.STRING)
    private ContentType contentType;
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MessageStatus messageStatus;
    @Column(name = "created_at")
    private Timestamp createdAt;
    @Column(name = "edited_at")
    private Timestamp editedAt;
}

