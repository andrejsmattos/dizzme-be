package com.dizzme.controller;

import com.dizzme.dto.ApiResponse;
import com.dizzme.dto.QRCodeResponse;
import com.dizzme.exception.BusinessException;
import com.dizzme.service.QRCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/qr")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
public class QRCodeController {

    @Autowired
    private QRCodeService qrCodeService;

    @GetMapping("/generate")
    public ResponseEntity<ApiResponse<QRCodeResponse>> generateQRCode(
            @RequestParam String url,
            @RequestParam(defaultValue = "300") Integer size) {
        try {
            QRCodeResponse qrCode = qrCodeService.generateQRCode(url, size);
            return ResponseEntity.ok(ApiResponse.success(qrCode));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/survey/{surveyPublicId}")
    public ResponseEntity<ApiResponse<QRCodeResponse>> generateSurveyQRCode(
            @PathVariable String surveyPublicId,
            @RequestParam(defaultValue = "300") Integer size) {
        try {
            QRCodeResponse qrCode = qrCodeService.generateSurveyQRCode(surveyPublicId, size);
            return ResponseEntity.ok(ApiResponse.success(qrCode));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}