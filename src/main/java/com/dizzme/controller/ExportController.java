package com.dizzme.controller;

import com.dizzme.dto.ExportRequest;
import com.dizzme.exception.BusinessException;
import com.dizzme.service.ExportService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/export")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:3000"})
@PreAuthorize("isAuthenticated()")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @PostMapping("/survey")
    public ResponseEntity<byte[]> exportSurveyData(@Valid @RequestBody ExportRequest request) {
        try {
            byte[] data = exportService.exportSurveyData(request);

            String filename = "survey_" + request.surveyId() + "_" +
                    System.currentTimeMillis() + "." + request.format().toLowerCase();

            String contentType = "xlsx".equalsIgnoreCase(request.format())
                    ? "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
                    : "text/csv";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType(contentType))
                    .body(data);

        } catch (BusinessException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
