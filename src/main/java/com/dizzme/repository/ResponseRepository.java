package com.dizzme.repository;

import com.dizzme.entity.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ResponseRepository extends JpaRepository<Response, Long> {

    List<Response> findBySurveyIdOrderBySubmittedAtDesc(Long surveyId);

    Page<Response> findBySurveyIdOrderBySubmittedAtDesc(Long surveyId, Pageable pageable);

    List<Response> findBySurveyPublicIdOrderBySubmittedAtDesc(String surveyPublicId);

    @Query("SELECT COUNT(r) FROM Response r WHERE r.survey.id = :surveyId")
    Integer countBySurveyId(@Param("surveyId") Long surveyId);

    @Query("SELECT COUNT(r) FROM Response r WHERE r.submittedAt >= :date")
    Integer countBySubmittedAtAfter(@Param("date") LocalDateTime date);

    @Query("SELECT COUNT(r) FROM Response r WHERE r.survey.client.id = :clientId")
    Integer countByClientId(@Param("clientId") Long clientId);

    @Query("SELECT r FROM Response r WHERE r.survey.id = :surveyId AND r.submittedAt BETWEEN :startDate AND :endDate ORDER BY r.submittedAt DESC")
    List<Response> findBySurveyIdAndDateRange(@Param("surveyId") Long surveyId,
                                              @Param("startDate") LocalDateTime startDate,
                                              @Param("endDate") LocalDateTime endDate);

    @Query("SELECT r FROM Response r WHERE r.survey.client.id = :clientId AND r.submittedAt >= :date ORDER BY r.submittedAt DESC")
    List<Response> findRecentByClientId(@Param("clientId") Long clientId, @Param("date") LocalDateTime date);

    @Query("SELECT MAX(r.submittedAt) FROM Response r WHERE r.survey.id = :surveyId")
    Optional<LocalDateTime> findLastResponseDateBySurveyId(@Param("surveyId") Long surveyId);
}