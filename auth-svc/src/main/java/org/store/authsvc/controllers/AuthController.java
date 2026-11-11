package org.store.authsvc.controllers;

import com.eduplatform.common.dtos.web.ReponseUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.store.authsvc.dtos.request.RegistorRequestDTO;
import org.store.authsvc.services.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register-student")
    public ResponseEntity<ReponseUtil<Void>> registerStudent(@Valid @RequestBody RegistorRequestDTO request) {
        authService.registerStudent(request);
        return ResponseEntity.status(HttpStatus.OK).body(ReponseUtil.success());
    }
}
