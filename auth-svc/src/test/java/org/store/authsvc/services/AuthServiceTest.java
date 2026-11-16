package org.store.authsvc.services;

import org.store.authsvc.entites.UsersEntity;
import com.eduplatform.common.exceptions.BaseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.store.authsvc.dtos.request.authentication.RegisterRequestDTO;
import org.store.authsvc.mappers.UserMapper;
import org.store.authsvc.repositories.UserRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordService passwordService;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;

    @Test
    void registersUserWithAnEncodedPassword() {
        RegisterRequestDTO request = validRequest();
        UsersEntity mappedUser = UsersEntity.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .displayName(request.getDisplayName())
                .yearOfBirth(request.getYearOfBirth())
                .homeTown(request.getHomeTown())
                .build();
        when(userMapper.toEntity(request)).thenReturn(mappedUser);
        when(passwordService.encode("A123456")).thenReturn("encoded-password");

        authService.registerUser(request);

        ArgumentCaptor<UsersEntity> userCaptor = ArgumentCaptor.forClass(UsersEntity.class);
        verify(userRepository).save(userCaptor.capture());
        UsersEntity user = userCaptor.getValue();
        assertThat(user.getUsername()).isEqualTo("VanNT_Buyer");
        assertThat(user.getEmail()).isEqualTo("nguyenthivanhs98@gmail.com");
        assertThat(user.getPassword()).isEqualTo("encoded-password");
        assertThat(user.getDisplayName()).isEqualTo("Nguyễn Thị Vân");
        assertThat(user.getYearOfBirth()).isEqualTo("1998");
        assertThat(user.getHomeTown()).isEqualTo("Hải Dương");
        verify(userMapper).toEntity(request);
    }

    @Test
    void rejectsDifferentPasswordConfirmation() {
        RegisterRequestDTO request = validRequest();
        request.setConfirmPassword("a123456");

        assertThatThrownBy(() -> authService.registerUser(request))
                .isInstanceOf(BaseException.class)
                .hasMessage("Password and confirm password do not match");

        verifyNoInteractions(userRepository, passwordService);
    }

    @Test
    void rejectsAnExistingUsername() {
        RegisterRequestDTO request = validRequest();
        when(userRepository.existsByUsername(request.getUsername())).thenReturn(true);

        assertThatThrownBy(() -> authService.registerUser(request))
                .isInstanceOf(BaseException.class)
                .hasMessage("Username already exists");

        verify(userRepository).existsByUsername(request.getUsername());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordService);
    }

    @Test
    void rejectsAnExistingEmail() {
        RegisterRequestDTO request = validRequest();
        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.registerUser(request))
                .isInstanceOf(BaseException.class)
                .hasMessage("Email already exists");

        verify(userRepository).existsByUsername(request.getUsername());
        verify(userRepository).existsByEmail(request.getEmail());
        verifyNoMoreInteractions(userRepository);
        verifyNoInteractions(passwordService);
    }

    private RegisterRequestDTO validRequest() {
        return new RegisterRequestDTO(
                "VanNT_Buyer", "nguyenthivanhs98@gmail.com", "A123456", "A123456",
                "Nguyễn Thị Vân", "STUDENT", "1998", "Hải Dương"
        );
    }
}
