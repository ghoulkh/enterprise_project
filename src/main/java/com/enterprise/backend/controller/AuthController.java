package com.enterprise.backend.controller;

import com.enterprise.backend.model.request.ChangePasswordRequest;
import com.enterprise.backend.model.request.LoginRequest;
import com.enterprise.backend.model.request.ResetPasswordRequest;
import com.enterprise.backend.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid LoginRequest request) {
        String token = authService.validateUsernamePasswordAndGenToken(request);
        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/change-password")
    public ResponseEntity<HttpStatus> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/forgot-mail/user/{username}")
    public ResponseEntity<HttpStatus> forgotPasswordByUsername(@PathVariable String username) {
        authService.forgotPasswordByUsername(username);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PostMapping("/reset-pass")
    public ResponseEntity<HttpStatus> resetPassByCode(@RequestBody @Valid ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
