package com.dizzme.repository;

import com.dizzme.entity.Survey;
import com.dizzme.entity.SurveyStatus;
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
public interface SurveyRepository extends JpaRepository<Survey, Long> {

    Optional<Survey> findByPublicId(String publicId);

    List<Survey> findByClientIdOrderByCreatedAtDesc(Long clientId);

    Page<Survey> findByClientIdOrderByCreatedAtDesc(Long clientId, Pageable pageable);

    List<Survey> findByClientIdAndActiveTrue(Long clientId);

    @Query("SELECT s FROM Survey s WHERE s.client.id = :clientId AND s.status = :status")
    List<Survey> findByClientIdAndStatus(@Param("clientId") Long clientId, @Param("status") SurveyStatus status);

    @Query("SELECT COUNT(s) FROM Survey s WHERE s.client.id = :clientId")
    Integer countByClientId(@Param("clientId") Long clientId);

    @Query("SELECT COUNT(s) FROM Survey s WHERE s.active = true")
    Integer countActiveSurveys();

    @Query("SELECT s FROM Survey s WHERE s.createdAt >= :date ORDER BY s.createdAt DESC")
    List<Survey> findRecentSurveys(@Param("date") LocalDateTime date);

    @Query("SELECT s FROM Survey s JOIN s.responses r WHERE r.submittedAt >= :date GROUP BY s ORDER BY COUNT(r) DESC")
    List<Survey> findMostActiveRecentSurveys(@Param("date") LocalDateTime date, Pageable pageable);
}
