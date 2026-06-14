package com.crm.tubes.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.crm.tubes.service.AuthService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthService.RegisterResponse> register(
            @RequestBody AuthService.RegisterRequest request) {

        return ResponseEntity.ok(
                authService.register(request)
        );
    }

    @PostMapping("/login")
    public ResponseEntity<AuthService.LoginResponse> login(
            @RequestBody AuthService.LoginRequest request,
            HttpSession session) {

        return ResponseEntity.ok(
                authService.login(request, session)
        );
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(
            HttpSession session) {

        authService.logout(session);

        return ResponseEntity.ok(
                "Logout berhasil"
        );
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(
            @RequestBody AuthService.ResetPasswordRequest request) {

        authService.resetPassword(request);

        return ResponseEntity.ok(
                "Password berhasil diubah"
        );
    }
}