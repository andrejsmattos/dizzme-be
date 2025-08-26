-- =================================================================
-- VIEWS FOR REPORTING AND ANALYTICS
-- =================================================================

-- View for NPS calculation
CREATE OR REPLACE VIEW vw_nps_scores AS
SELECT
    s.id as survey_id,
    s.title as survey_title,
    COUNT(a.id) as total_responses,
    COUNT(CASE WHEN CAST(a.value AS INTEGER) >= 9 THEN 1 END) as promoters,
    COUNT(CASE WHEN CAST(a.value AS INTEGER) BETWEEN 7 AND 8 THEN 1 END) as passives,
    COUNT(CASE WHEN CAST(a.value AS INTEGER) <= 6 THEN 1 END) as detractors,
    ROUND(
        ((COUNT(CASE WHEN CAST(a.value AS INTEGER) >= 9 THEN 1 END) -
          COUNT(CASE WHEN CAST(a.value AS INTEGER) <= 6 THEN 1 END))::DECIMAL /
         NULLIF(COUNT(a.id), 0)) * 100, 2
    ) as nps_score
FROM surveys s
JOIN questions q ON s.id = q.survey_id
JOIN answers a ON q.id = a.question_id
JOIN responses r ON a.response_id = r.id
WHERE q.type = 'NPS'
  AND a.value ~ '^[0-9]+$'
  AND CAST(a.value AS INTEGER) BETWEEN 0 AND 10
GROUP BY s.id, s.title;

-- View for CSAT calculation
CREATE OR REPLACE VIEW vw_csat_scores AS
SELECT
    s.id as survey_id,
    s.title as survey_title,
    ROUND(AVG(CAST(a.value AS NUMERIC)), 2) as csat_score,
    COUNT(a.id) as total_responses
FROM surveys s
JOIN questions q ON s.id = q.survey_id
JOIN answers a ON q.id = a.question_id
WHERE q.type = 'CSAT'
  AND a.value ~ '^[0-9]+$'
GROUP BY s.id, s.title;
