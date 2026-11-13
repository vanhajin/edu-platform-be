package org.store.authsvc.dtos.persistence;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Persistence DTO corresponding to {@code UserSessionsEntity}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserSessionsDTO {

    private String userId;
    private String refreshTokenHash;
    private String deviceId;
    private String deviceInfo;
    private String ipAddress;
    private boolean revoked;
    private LocalDateTime revokedAt;
    private LocalDateTime expiresAt;
    private LocalDateTime lastUsedDate;
}
