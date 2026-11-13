package org.store.authsvc.services;

import com.eduplatform.common.exceptions.BaseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import jakarta.servlet.http.HttpServletRequest;
import org.store.authsvc.dtos.persistence.IUserLoginDTO;
import org.store.authsvc.dtos.request.LoginRequestDTO;
import org.store.authsvc.dtos.request.RefreshTokenRequestDTO;
import org.store.authsvc.dtos.response.LoginResponseDTO;
import org.store.authsvc.entites.UserSessionsEntity;
import org.store.authsvc.mappers.UserMapper;
import org.store.authsvc.mappers.UserSessionsMap;
import org.store.authsvc.repositories.UserRepository;
import org.store.authsvc.repositories.UserSessionsRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceLoginTest {

    @Mock private UserRepository userRepository;
    @Mock private UserSessionsRepository userSessionsRepository;
    @Mock private PasswordService passwordService;
    @Mock private UserMapper userMapper;
    @Mock private UserSessionsMap userSessionsMap;
    @Mock private TokenService tokenService;
    @Mock private HttpServletRequest httpServletRequest;
    @InjectMocks private AuthService authService;

    @Test
    void logsInActiveUserAndStoresOnlyRefreshTokenHash() {
        LoginRequestDTO request = new LoginRequestDTO("student@example.com", "A123456", "550e8400-e29b-41d4-a716-446655440000");
        IUserLoginDTO user = mock(IUserLoginDTO.class);
        when(user.getUserId()).thenReturn("user-id");
        when(user.getUsername()).thenReturn("student");
        when(user.getDisplayName()).thenReturn("Student One");
        when(user.getEmail()).thenReturn("student@example.com");
        when(user.getRole()).thenReturn("STUDENT");
        when(user.getPassword()).thenReturn("bcrypt-hash");
        when(userRepository.findUserInfoByUsernameOrEmail(request.getAccount())).thenReturn(Optional.of(user));
        when(passwordService.matches(request.getPassword(), "bcrypt-hash")).thenReturn(true);
        when(tokenService.generateAccessToken("user-id", "student", "STUDENT")).thenReturn("access-token");
        when(tokenService.generateRefreshToken()).thenReturn("raw-refresh-token");
        when(tokenService.hashRefreshToken("raw-refresh-token")).thenReturn("refresh-token-hash");
        when(tokenService.refreshTokenExpiresAt()).thenReturn(LocalDateTime.of(2026, 12, 13, 10, 0));
        when(tokenService.accessTokenExpirationSeconds()).thenReturn(900L);
        when(httpServletRequest.getHeader("User-Agent")).thenReturn("Chrome on Windows");
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        UserSessionsEntity entity = new UserSessionsEntity();
        when(userSessionsMap.toEntity(any())).thenReturn(entity);

        LoginResponseDTO response = authService.login(request, httpServletRequest);

        ArgumentCaptor<org.store.authsvc.dtos.persistence.UserSessionsDTO> sessionCaptor =
                ArgumentCaptor.forClass(org.store.authsvc.dtos.persistence.UserSessionsDTO.class);
        verify(userSessionsMap).toEntity(sessionCaptor.capture());
        verify(userSessionsRepository).save(entity);
        assertThat(sessionCaptor.getValue().getRefreshTokenHash()).isEqualTo("refresh-token-hash");
        assertThat(sessionCaptor.getValue().getUserId()).isEqualTo("user-id");
        assertThat(sessionCaptor.getValue().getDeviceId()).isEqualTo("550e8400-e29b-41d4-a716-446655440000");
        assertThat(sessionCaptor.getValue().getDeviceInfo()).isEqualTo("Chrome on Windows");
        assertThat(sessionCaptor.getValue().getIpAddress()).isEqualTo("127.0.0.1");
        assertThat(response.getAccessToken()).isEqualTo("access-token");
        assertThat(response.getRefreshToken()).isEqualTo("raw-refresh-token");
        assertThat(response.getExpiresIn()).isEqualTo(900);
        assertThat(response.getRefreshExpiresAt()).isEqualTo(LocalDateTime.of(2026, 12, 13, 10, 0));
        assertThat(response.getInfo().getEmail()).isEqualTo("student@example.com");
    }

    @Test
    void rejectsUnknownAccountWithoutCreatingSession() {
        LoginRequestDTO request = new LoginRequestDTO("missing", "A123456");
        when(userRepository.findUserInfoByUsernameOrEmail("missing")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request, httpServletRequest)).isInstanceOf(BaseException.class);

        verifyNoInteractions(passwordService, tokenService, userSessionsMap, userSessionsRepository);
    }

    @Test
    void rejectsInvalidPasswordWithoutCreatingSession() {
        LoginRequestDTO request = new LoginRequestDTO("student", "wrong-password");
        IUserLoginDTO user = mock(IUserLoginDTO.class);
        when(user.getPassword()).thenReturn("bcrypt-hash");
        when(userRepository.findUserInfoByUsernameOrEmail("student")).thenReturn(Optional.of(user));
        when(passwordService.matches("wrong-password", "bcrypt-hash")).thenReturn(false);

        assertThatThrownBy(() -> authService.login(request, httpServletRequest)).isInstanceOf(BaseException.class);

        verifyNoInteractions(tokenService, userSessionsMap, userSessionsRepository);
    }

    @Test
    void updatesExistingSessionForTheSameUserAndDevice() {
        LoginRequestDTO request = new LoginRequestDTO("student", "A123456", "550e8400-e29b-41d4-a716-446655440000");
        IUserLoginDTO user = mock(IUserLoginDTO.class);
        UserSessionsEntity existingSession = mock(UserSessionsEntity.class);
        when(user.getUserId()).thenReturn("user-id");
        when(user.getUsername()).thenReturn("student");
        when(user.getDisplayName()).thenReturn("Student One");
        when(user.getEmail()).thenReturn("student@example.com");
        when(user.getRole()).thenReturn("STUDENT");
        when(user.getPassword()).thenReturn("bcrypt-hash");
        when(userRepository.findUserInfoByUsernameOrEmail("student")).thenReturn(Optional.of(user));
        when(passwordService.matches("A123456", "bcrypt-hash")).thenReturn(true);
        when(tokenService.generateAccessToken("user-id", "student", "STUDENT")).thenReturn("access-token");
        when(tokenService.generateRefreshToken()).thenReturn("new-refresh-token");
        when(tokenService.hashRefreshToken("new-refresh-token")).thenReturn("new-token-hash");
        when(tokenService.refreshTokenExpiresAt()).thenReturn(LocalDateTime.of(2026, 12, 13, 10, 0));
        when(tokenService.currentTime()).thenReturn(LocalDateTime.of(2026, 11, 13, 10, 0));
        when(tokenService.accessTokenExpirationSeconds()).thenReturn(900L);
        when(httpServletRequest.getHeader("User-Agent")).thenReturn("Chrome on Windows");
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(userSessionsRepository.findByUserIdAndDeviceId("user-id", request.getDeviceId()))
                .thenReturn(Optional.of(existingSession));

        authService.login(request, httpServletRequest);

        verify(existingSession).setRefreshTokenHash("new-token-hash");
        verify(existingSession).setRevoked(false);
        verify(existingSession).setRevokedAt(null);
        verify(userSessionsRepository).save(existingSession);
        verifyNoInteractions(userSessionsMap);
    }

    @Test
    void refreshesTokensAndRevokesThePreviousSession() {
        RefreshTokenRequestDTO request = new RefreshTokenRequestDTO("old-refresh-token");
        UserSessionsEntity oldSession = mock(UserSessionsEntity.class);
        IUserLoginDTO user = mock(IUserLoginDTO.class);
        when(tokenService.hashRefreshToken("old-refresh-token")).thenReturn("old-token-hash");
        when(tokenService.currentTime()).thenReturn(LocalDateTime.of(2026, 11, 13, 10, 0));
        when(userSessionsRepository.findByRefreshTokenHashAndRevokedFalseAndExpiresAtAfter(
                "old-token-hash", LocalDateTime.of(2026, 11, 13, 10, 0))).thenReturn(Optional.of(oldSession));
        when(oldSession.getUserId()).thenReturn("user-id");
        when(oldSession.getDeviceId()).thenReturn("550e8400-e29b-41d4-a716-446655440000");
        when(userRepository.findByUserIdAndStatus("user-id", 1)).thenReturn(Optional.of(user));
        when(user.getUserId()).thenReturn("user-id");
        when(user.getUsername()).thenReturn("student");
        when(user.getDisplayName()).thenReturn("Student One");
        when(user.getEmail()).thenReturn("student@example.com");
        when(user.getRole()).thenReturn("STUDENT");
        when(tokenService.generateAccessToken("user-id", "student", "STUDENT")).thenReturn("new-access-token");
        when(tokenService.generateRefreshToken()).thenReturn("new-refresh-token");
        when(tokenService.hashRefreshToken("new-refresh-token")).thenReturn("new-token-hash");
        when(tokenService.refreshTokenExpiresAt()).thenReturn(LocalDateTime.of(2026, 12, 13, 10, 0));
        when(tokenService.accessTokenExpirationSeconds()).thenReturn(900L);
        when(httpServletRequest.getHeader("User-Agent")).thenReturn("Chrome on Windows");
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(userSessionsMap.toEntity(any())).thenReturn(new UserSessionsEntity());

        LoginResponseDTO response = authService.refresh(request, httpServletRequest);

        verify(oldSession).setRevoked(true);
        verify(oldSession).setRevokedAt(LocalDateTime.of(2026, 11, 13, 10, 0));
        verify(userSessionsRepository).save(oldSession);
        assertThat(response.getAccessToken()).isEqualTo("new-access-token");
        assertThat(response.getRefreshToken()).isEqualTo("new-refresh-token");
    }
}
