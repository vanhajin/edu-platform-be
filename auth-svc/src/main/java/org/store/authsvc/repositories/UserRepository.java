package org.store.authsvc.repositories;

import com.eduplatform.common.entites.UsersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.store.authsvc.dtos.persistence.IUserLoginDTO;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UsersEntity, String> {
    // Kiểm tra trùng lặp độc lập cho từng trường
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<IUserLoginDTO> findByUsernameIgnoreCaseAndStatusOrEmailIgnoreCaseAndStatus(
            String username,
            int usernameStatus,
            String email,
            int emailStatus
    );

    default Optional<IUserLoginDTO> findUserInfoByUsernameOrEmail(String account) {
        return findByUsernameIgnoreCaseAndStatusOrEmailIgnoreCaseAndStatus(account, 1, account, 1);
    }

    Optional<IUserLoginDTO> findByUserIdAndStatus(String userId, int status);
}
