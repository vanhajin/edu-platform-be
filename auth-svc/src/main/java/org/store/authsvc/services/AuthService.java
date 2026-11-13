package org.store.authsvc.services;

import com.eduplatform.common.entites.UsersEntity;
import com.eduplatform.common.exceptions.BaseException;
import com.eduplatform.common.utils.enums.EnumRole;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.store.authsvc.dtos.persistence.IUserLoginDTO;
import org.store.authsvc.dtos.persistence.UserSessionsDTO;
import org.store.authsvc.dtos.request.LoginRequestDTO;
import org.store.authsvc.dtos.request.RefreshTokenRequestDTO;
import org.store.authsvc.dtos.request.RegisterRequestDTO;
import org.store.authsvc.dtos.response.LoginResponseDTO;
import org.store.authsvc.dtos.response.UserInfoResponseDTO;
import org.store.authsvc.entites.UserSessionsEntity;
import org.store.authsvc.mappers.UserMapper;
import org.store.authsvc.mappers.UserSessionsMap;
import org.store.authsvc.repositories.UserRepository;
import org.store.authsvc.repositories.UserSessionsRepository;
import org.store.authsvc.utils.enums.EnumAuthError;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;

    private final UserSessionsRepository userSessionsRepository;

    private final PasswordService passwordService;

    private final UserMapper userMapper;

    private final UserSessionsMap userSessionsMap;

    private final TokenService tokenService;

    @Transactional
    public LoginResponseDTO login(LoginRequestDTO request, HttpServletRequest httpServletRequest) {
        IUserLoginDTO user = userRepository.findUserInfoByUsernameOrEmail(request.getAccount())
                .orElseThrow(this::loginFailure);

        if (!passwordService.matches(request.getPassword(), user.getPassword())) {
            throw loginFailure();
        }

        return createLoginResponse(user, request.getDeviceId(), httpServletRequest);
    }

    @Transactional
    public LoginResponseDTO refresh(RefreshTokenRequestDTO request, HttpServletRequest httpServletRequest) {
        LocalDateTime now = tokenService.currentTime();
        UserSessionsEntity oldSession = userSessionsRepository
                .findByRefreshTokenHashAndRevokedFalseAndExpiresAtAfter(
                        tokenService.hashRefreshToken(request.getRefreshToken()),
                        now
                )
                .orElseThrow(this::refreshFailure);

        IUserLoginDTO user = userRepository.findByUserIdAndStatus(oldSession.getUserId(), 1)
                .orElseThrow(this::refreshFailure);

        oldSession.setRevoked(true);
        oldSession.setRevokedAt(now);
        oldSession.setLastUsedDate(now);
        userSessionsRepository.save(oldSession);

        return createLoginResponse(user, oldSession.getDeviceId(), httpServletRequest);
    }

    private LoginResponseDTO createLoginResponse(
            IUserLoginDTO user,
            String deviceId,
            HttpServletRequest httpServletRequest
    ) {
        String accessToken = tokenService.generateAccessToken(user.getUserId(), user.getUsername(), user.getRole());
        String refreshToken = tokenService.generateRefreshToken();
        LocalDateTime refreshExpiresAt = tokenService.refreshTokenExpiresAt();
        LocalDateTime now = tokenService.currentTime();
        UserSessionsDTO session = UserSessionsDTO.builder()
                .userId(user.getUserId())
                .refreshTokenHash(tokenService.hashRefreshToken(refreshToken))
                .deviceId(deviceId)
                .deviceInfo(truncate(httpServletRequest.getHeader("User-Agent"), 500))
                .ipAddress(httpServletRequest.getRemoteAddr())
                .expiresAt(refreshExpiresAt)
                .lastUsedDate(now)
                .build();
        UserSessionsEntity sessionEntity = deviceId == null
                ? userSessionsMap.toEntity(session)
                : userSessionsRepository.findByUserIdAndDeviceId(user.getUserId(), deviceId)
                .map(existingSession -> updateSession(existingSession, session))
                .orElseGet(() -> userSessionsMap.toEntity(session));
        userSessionsRepository.save(sessionEntity);

        UserInfoResponseDTO userInfo = new UserInfoResponseDTO(
                user.getUserId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getEmail(),
                user.getRole()
        );
        return new LoginResponseDTO(
                accessToken,
                refreshToken,
                "Bearer",
                tokenService.accessTokenExpirationSeconds(),
                refreshExpiresAt,
                userInfo
        );
    }

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

    private BaseException loginFailure() {
        return new BaseException(EnumAuthError.LOGIN_ACCOUNT_PASSWORD_FAIL.getMa(),
                EnumAuthError.LOGIN_ACCOUNT_PASSWORD_FAIL.getMoTa());
    }

    private BaseException refreshFailure() {
        return new BaseException(EnumAuthError.REFRESH_TOKEN_INVALID.getMa(),
                EnumAuthError.REFRESH_TOKEN_INVALID.getMoTa());
    }

    private String truncate(String value, int maximumLength) {
        if (value == null || value.length() <= maximumLength) {
            return value;
        }
        return value.substring(0, maximumLength);
    }

    private UserSessionsEntity updateSession(UserSessionsEntity existingSession, UserSessionsDTO session) {
        existingSession.setRefreshTokenHash(session.getRefreshTokenHash());
        existingSession.setDeviceInfo(session.getDeviceInfo());
        existingSession.setIpAddress(session.getIpAddress());
        existingSession.setRevoked(false);
        existingSession.setRevokedAt(null);
        existingSession.setExpiresAt(session.getExpiresAt());
        existingSession.setLastUsedDate(session.getLastUsedDate());
        return existingSession;
    }
}
