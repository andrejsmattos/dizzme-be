package com.dizzme.controller;

import com.dizzme.dto.ApiResponse;
import com.dizzme.dto.AuthResponse;
import com.dizzme.dto.LoginRequest;
import com.dizzme.dto.RegisterRequest;
import com.dizzme.exception.BusinessException;
import com.dizzme.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.register(request);
            return ResponseEntity.ok(ApiResponse.success("Conta criada com sucesso", response));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success("Login realizado com sucesso", response));
        } catch (BusinessException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> logout() {
        return ResponseEntity.ok(ApiResponse.success("Logout realizado com sucesso", null));
    }

    // *** NOVO ENDPOINT: Criar Admin (apenas para desenvolvimento/teste) ***
    @PostMapping("/create-admin")
    public ResponseEntity<ApiResponse<AuthResponse>> createAdmin(@Valid @RequestBody RegisterRequest request) {
        try {
            AuthResponse response = authService.createAdmin(request);
            return ResponseEntity.ok(ApiResponse.success("Admin criado com sucesso", response));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}