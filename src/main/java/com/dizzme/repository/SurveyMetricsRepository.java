package com.dizzme.repository;

import java.util.List;

public interface SurveyMetricsRepository {

    Double calculateNpsBySurveyId(Long surveyId);

    Double calculateCsatBySurveyId(Long surveyId);

    Double calculateCesBySurveyId(Long surveyId);

    List<Object[]> getMonthlyResponseStats(Long clientId, int months);

    List<Object[]> getQuestionTypeDistribution(Long surveyId);
}
