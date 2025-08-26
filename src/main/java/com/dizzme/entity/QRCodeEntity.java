package com.dizzme.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "qr_codes")
public class QRCodeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String surveyPublicId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String base64Image;

    @Column(nullable = false)
    private Integer size;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Constructors, getters and setters
    public QRCodeEntity() {}

    public QRCodeEntity(String surveyPublicId, String base64Image, Integer size) {
        this.surveyPublicId = surveyPublicId;
        this.base64Image = base64Image;
        this.size = size;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSurveyPublicId() { return surveyPublicId; }
    public void setSurveyPublicId(String surveyPublicId) { this.surveyPublicId = surveyPublicId; }

    public String getBase64Image() { return base64Image; }
    public void setBase64Image(String base64Image) { this.base64Image = base64Image; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public LocalDateTime getCreatedAt() { return createdAt; }
}
