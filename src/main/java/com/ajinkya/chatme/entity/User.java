package com.ajinkya.chatme.entity;

import com.ajinkya.chatme.common.enums.UserStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "User", schema = "auth")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {
    @Id
    @Column(name="user_id")
    @GeneratedValue(strategy=GenerationType.UUID)
    private UUID id;
    @Column(name="email")
    private String email;
    @Column(name="avatar_url")
    private String avatarUrl;
    @Column(name="status")
    @Enumerated(EnumType.STRING)
    private UserStatus status;
    @Column(name="last_seen")
    private Timestamp lastSeen;
    @Column(name="insert_dt")
    private Timestamp insertDt;
    @Column(name="username")
    private String username;
    @Column(name="password_hash")
    private String password;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }
}
