package org.store.authsvc.services;

import com.eduplatform.common.entites.UsersEntity;
import com.eduplatform.common.exceptions.BaseException;
import com.eduplatform.common.utils.enums.EnumRole;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.store.authsvc.dtos.request.RegisterRequestDTO;
import org.store.authsvc.mappers.UserMapper;
import org.store.authsvc.repositories.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final PasswordService passwordService;

    private final UserMapper userMapper;

    @Transactional
    @SneakyThrows
    public void registerStudent(RegisterRequestDTO request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BaseException("USERNAME_ALREADY_EXISTS", "Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BaseException("EMAIL_ALREADY_EXISTS", "Email already exists");
        }

        toRole(request.getRole());
        UsersEntity user = userMapper.toEntity(request);
        user.setPassword(passwordService.encode(request.getPassword()));

        userRepository.save(user);
    }

    private EnumRole toRole(String role) {
        try {
            return EnumRole.valueOf(role);
        } catch (IllegalArgumentException exception) {
            throw new BaseException("INVALID_ROLE", "Role is invalid");
        }
    }
}
