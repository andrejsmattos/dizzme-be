package com.dizzme.controller;

import com.dizzme.dto.ApiResponse;
import com.dizzme.dto.ResponseDto;
import com.dizzme.dto.ResponseSubmitRequest;
import com.dizzme.exception.BusinessException;
import com.dizzme.service.ResponseService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/responses")
public class ResponseController {

    @Autowired
    private ResponseService responseService;

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<String>> submitResponse(
            @Valid @RequestBody ResponseSubmitRequest request,
            HttpServletRequest httpRequest) {
        try {
            String userIp = getClientIpAddress(httpRequest);
            String userAgent = httpRequest.getHeader("User-Agent");

            responseService.submitResponse(request, userIp, userAgent);
            return ResponseEntity.ok(ApiResponse.success("Resposta enviada com sucesso", null));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/survey/{surveyId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<ResponseDto>>> getSurveyResponses(@PathVariable Long surveyId) {
        try {
            List<ResponseDto> responses = responseService.getSurveyResponses(surveyId);
            return ResponseEntity.ok(ApiResponse.success(responses));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader == null) {
            return request.getRemoteAddr();
        } else {
            return xForwardedForHeader.split(",")[0];
        }
    }
}

