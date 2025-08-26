package com.dizzme.repository;

import com.dizzme.entity.QRCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface QRCodeRepository extends JpaRepository<QRCodeEntity, Long> {

    Optional<QRCodeEntity> findBySurveyPublicId(String surveyPublicId);

    void deleteBySurveyPublicId(String surveyPublicId);
}