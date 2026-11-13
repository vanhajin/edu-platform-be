package org.store.authsvc.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.store.authsvc.entites.UserSessionsEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserSessionsRepository extends JpaRepository<UserSessionsEntity, String> {

    Optional<UserSessionsEntity> findByRefreshTokenHashAndRevokedFalseAndExpiresAtAfter(
            String refreshTokenHash,
            LocalDateTime now
    );

    List<UserSessionsEntity> findByUserIdAndRevokedFalseAndExpiresAtAfter(
            String userId,
            LocalDateTime now
    );

    Optional<UserSessionsEntity> findByUserIdAndDeviceId(String userId, String deviceId);
}
