package com.dizzme.controller;

import com.dizzme.dto.*;
import com.dizzme.exception.BusinessException;
import com.dizzme.service.SurveyService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/surveys")
public class SurveyController {

    @Autowired
    private SurveyService surveyService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SurveyDto>> createSurvey(@Valid @RequestBody SurveyCreateRequest request) {
        try {
            SurveyDto survey = surveyService.createSurvey(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Pesquisa criada com sucesso", survey));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<SurveySummaryDto>>> getMySurveys() {
        try {
            List<SurveySummaryDto> surveys = surveyService.getMySurveys();
            return ResponseEntity.ok(ApiResponse.success(surveys));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SurveyDto>> getSurvey(@PathVariable Long id) {
        try {
            SurveyDto survey = surveyService.getSurvey(id);
            return ResponseEntity.ok(ApiResponse.success(survey));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/public/{publicId}")
    public ResponseEntity<ApiResponse<SurveyDto>> getPublicSurvey(@PathVariable String publicId) {
        try {
            SurveyDto survey = surveyService.getPublicSurvey(publicId);
            return ResponseEntity.ok(ApiResponse.success(survey));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<SurveyDto>> updateSurvey(
            @PathVariable Long id,
            @Valid @RequestBody SurveyUpdateRequest request) {
        try {
            SurveyDto survey = surveyService.updateSurvey(id, request);
            return ResponseEntity.ok(ApiResponse.success("Pesquisa atualizada com sucesso", survey));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> deleteSurvey(@PathVariable Long id) {
        try {
            surveyService.deleteSurvey(id);
            return ResponseEntity.ok(ApiResponse.success("Pesquisa excluída com sucesso", null));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
