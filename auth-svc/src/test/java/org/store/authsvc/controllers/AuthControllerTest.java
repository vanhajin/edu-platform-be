package org.store.authsvc.controllers;

import com.eduplatform.common.dtos.web.ReponseUtil;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.store.authsvc.dtos.request.authentication.RegisterRequestDTO;
import org.store.authsvc.services.AuthService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AuthControllerTest {

    @Test
    void registersStudentAndReturnsCreatedResponse() {
        AuthService authService = mock(AuthService.class);
        AuthController controller = new AuthController(authService);
        RegisterRequestDTO request = new RegisterRequestDTO(
                "VanNT_Buyer", "nguyenthivanhs98@gmail.com", "A123456", "A123456",
                "Nguyễn Thị Vân", "STUDENT", "1998", "Hải Dương"
        );
        ReponseUtil<Void> expectedResponse = ReponseUtil.success(null);
        when(authService.registerStudent(request)).thenReturn(expectedResponse);

        ResponseEntity<ReponseUtil<Void>> response = controller.registerStudent(request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isSameAs(expectedResponse);
        verify(authService).registerStudent(request);
    }
}
