package com.example.coursemanagement_finalprojectmodule03.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "revoked_tokens")
public class RevokedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "token_id")
    private Integer id;

    @Column(name = "token", nullable = false, unique = true, length = 500)
    private String token;

    @CreationTimestamp
    @Column(name = "revoked_at", nullable = false, updatable = false)
    private LocalDateTime revokedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}