package com.dizzme.controller;

import com.dizzme.dto.ApiResponse;
import com.dizzme.dto.DashboardStatsDto;
import com.dizzme.dto.SurveyStatsDto;
import com.dizzme.exception.BusinessException;
import com.dizzme.service.DashboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard")
@PreAuthorize("isAuthenticated()")
public class DashboardController {

    @Autowired
    private DashboardService dashboardService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<DashboardStatsDto>> getDashboardStats() {
        try {
            DashboardStatsDto stats = dashboardService.getClientDashboardStats();
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/survey/{surveyId}/stats")
    public ResponseEntity<ApiResponse<SurveyStatsDto>> getSurveyStats(@PathVariable Long surveyId) {
        try {
            SurveyStatsDto stats = dashboardService.getSurveyStats(surveyId);
            return ResponseEntity.ok(ApiResponse.success(stats));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}