package com.ajinkya.chatme.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.context.annotation.Lazy;

import java.util.UUID;

@Entity
@Table(name = "attachment", schema = "message")
@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Attachment {
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "attachment_id")
    @Id
    private UUID id;
    @Lazy
    @JoinColumn(name = "message_id")
    @ManyToOne
    private Message message;
    @Column(name = "file_url")
    private String fileUrl;
    @Column(name = "file_type")
    private String fileType;
    @Column(name = "file_size")
    private Integer fileSize;
}
