package org.store.authsvc.controllers;

import com.eduplatform.common.dtos.web.ReponseUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.store.authsvc.dtos.request.authentication.RegisterRequestDTO;
import org.store.authsvc.dtos.request.authentication.LoginRequestDTO;
import org.store.authsvc.dtos.request.authentication.RefreshTokenRequestDTO;
import org.store.authsvc.dtos.response.LoginResponseDTO;
import org.store.authsvc.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register-student")
    public ResponseEntity<ReponseUtil<Void>> registerStudent(@Valid @RequestBody RegisterRequestDTO request) {
        authService.registerStudent(request);
        return ResponseEntity.status(HttpStatus.OK).body(ReponseUtil.success());
    }

    @PostMapping("/login")
    public ResponseEntity<ReponseUtil<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO request,
            HttpServletRequest httpServletRequest
    ) {
        return ResponseEntity.ok(ReponseUtil.success(authService.login(request, httpServletRequest)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ReponseUtil<LoginResponseDTO>> refresh(
            @Valid @RequestBody RefreshTokenRequestDTO request,
            HttpServletRequest httpServletRequest
    ) {
        return ResponseEntity.ok(ReponseUtil.success(authService.refresh(request, httpServletRequest)));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @org.springframework.web.bind.annotation.RequestHeader(
                    value = HttpHeaders.AUTHORIZATION,
                    required = false
            ) String authorization
    ) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Missing or invalid access token"
            );
        }

        authService.logout(authorization.substring(7));
        return ResponseEntity.noContent().build();
    }
}
