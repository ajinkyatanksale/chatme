package com.ajinkya.chatme.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;

import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "message_read", schema = "message")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class MessageRead {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "message_read_id")
    private UUID id;
    @Lazy
    @ManyToOne
    @JoinColumn(name = "message_id")
    private Message message;
    @Lazy
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "read_at")
    private Timestamp readAt;
}
