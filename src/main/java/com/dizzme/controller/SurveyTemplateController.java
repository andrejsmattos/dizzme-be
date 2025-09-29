package com.dizzme.controller;

import com.dizzme.dto.ApiResponse;
import com.dizzme.dto.SurveyTemplate;
import com.dizzme.exception.BusinessException;
import com.dizzme.service.SurveyTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/templates")
@PreAuthorize("isAuthenticated()")
public class SurveyTemplateController {

    @Autowired
    private SurveyTemplateService templateService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SurveyTemplate>>> getTemplates() {
        try {
            List<SurveyTemplate> templates = templateService.getTemplates();
            return ResponseEntity.ok(ApiResponse.success(templates));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
