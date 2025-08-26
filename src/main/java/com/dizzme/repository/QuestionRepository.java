package com.dizzme.repository;

import com.dizzme.entity.Question;
import com.dizzme.entity.QuestionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findBySurveyIdOrderByQuestionOrder(Long surveyId);

    List<Question> findBySurveyPublicIdOrderByQuestionOrder(String surveyPublicId);

    @Query("SELECT q FROM Question q WHERE q.survey.id = :surveyId AND q.type = :type")
    List<Question> findBySurveyIdAndType(@Param("surveyId") Long surveyId, @Param("type") QuestionType type);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.survey.id = :surveyId")
    Integer countBySurveyId(@Param("surveyId") Long surveyId);

    void deleteBySurveyId(Long surveyId);
}