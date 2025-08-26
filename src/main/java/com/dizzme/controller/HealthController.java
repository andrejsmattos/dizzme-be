package com.dizzme.controller;

import com.dizzme.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/health")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class HealthController {

    @GetMapping
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Dizzme API is running", "OK"));
    }

    @GetMapping("/version")
    public ResponseEntity<ApiResponse<String>> version() {
        return ResponseEntity.ok(ApiResponse.success("1.0.0"));
    }
}