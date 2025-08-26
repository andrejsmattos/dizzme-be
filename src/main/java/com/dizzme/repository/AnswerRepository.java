package com.dizzme.repository;

import com.dizzme.entity.Answer;
import com.dizzme.entity.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {

    List<Answer> findByResponseId(Long responseId);

    List<Answer> findByQuestionId(Long questionId);

    @Query("SELECT a FROM Answer a WHERE a.question.survey.id = :surveyId ORDER BY a.response.submittedAt DESC")
    List<Answer> findBySurveyId(@Param("surveyId") Long surveyId);

    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId AND a.question.type = :questionType")
    List<Answer> findByQuestionIdAndType(@Param("questionId") Long questionId, @Param("questionType") QuestionType questionType);

    // Specific queries for metrics calculation
    @Query("SELECT a.value, COUNT(a) FROM Answer a WHERE a.question.id = :questionId GROUP BY a.value ORDER BY COUNT(a) DESC")
    List<Object[]> findAnswerDistributionByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT AVG(CAST(a.value AS double)) FROM Answer a WHERE a.question.id = :questionId AND a.question.type IN ('NPS', 'CSAT', 'CES')")
    Optional<Double> findAverageScoreByQuestionId(@Param("questionId") Long questionId);

    // NPS specific queries
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question.id = :questionId AND a.question.type = 'NPS' AND CAST(a.value AS int) >= 9")
    Integer countNpsPromoters(@Param("questionId") Long questionId);

    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question.id = :questionId AND a.question.type = 'NPS' AND CAST(a.value AS int) BETWEEN 7 AND 8")
    Integer countNpsPassives(@Param("questionId") Long questionId);

    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question.id = :questionId AND a.question.type = 'NPS' AND CAST(a.value AS int) <= 6")
    Integer countNpsDetractors(@Param("questionId") Long questionId);

    // Text answers for word cloud generation
    @Query("SELECT a.value FROM Answer a WHERE a.question.id = :questionId AND a.question.type = 'TEXT' AND LENGTH(a.value) > 0")
    List<String> findTextAnswersByQuestionId(@Param("questionId") Long questionId);

    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question.id = :questionId")
    Integer countByQuestionId(@Param("questionId") Long questionId);

    void deleteByResponseId(Long responseId);
}
