package org.store.authsvc.entites;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "user_sessions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_session_token",
                columnNames = "refresh_token_hash"
        ),
        indexes = {
                @Index(name = "idx_user_active_session", columnList = "user_id, is_revoked, expires_at"),
                @Index(name = "idx_session_expiry", columnList = "expires_at")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionsEntity {

    @Id
    @UuidGenerator
    @Column(name = "user_session_id", nullable = false, length = 40)
    private String userSessionId;

    @Column(name = "user_id", nullable = false, length = 40)
    private String userId;

    @Column(name = "refresh_token_hash", nullable = false, length = 64)
    private String refreshTokenHash;

    @Column(name = "device_id", length = 36)
    private String deviceId;

    @Column(name = "device_info", length = 500)
    private String deviceInfo;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "is_revoked", nullable = false)
    @Builder.Default
    private boolean revoked = false;

    @Column(name = "revoked_at")
    private LocalDateTime revokedAt;

    @Column(name = "expires_at", nullable = false, columnDefinition = "datetime(6)")
    private LocalDateTime expiresAt;

    @Column(name = "last_used_date", columnDefinition = "timestamp(6)")
    private LocalDateTime lastUsedDate;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false, columnDefinition = "timestamp(6)")
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date", nullable = false, columnDefinition = "timestamp(6)")
    private LocalDateTime updatedDate;
}
