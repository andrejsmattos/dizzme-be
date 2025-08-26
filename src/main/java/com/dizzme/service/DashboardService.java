package com.dizzme.service;

import com.dizzme.dto.DashboardStatsDto;
import com.dizzme.dto.QuestionStatsDto;
import com.dizzme.dto.SurveyStatsDto;
import com.dizzme.entity.Question;
import com.dizzme.entity.QuestionType;
import com.dizzme.entity.Survey;
import com.dizzme.entity.Client;
import com.dizzme.exception.BusinessException;
import com.dizzme.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ClientRepository clientRepository;

    public DashboardStatsDto getClientDashboardStats() throws BusinessException {
        Long clientId = getCurrentClientId();

        List<Survey> surveys = surveyRepository.findByClientIdOrderByCreatedAtDesc(clientId);
        Integer totalSurveys = surveys.size();
        Integer activeSurveys = (int) surveys.stream().filter(Survey::getActive).count();

        Integer totalResponses = responseRepository.countByClientId(clientId);
        Integer responsesToday = responseRepository.countBySubmittedAtAfter(
                LocalDateTime.now().toLocalDate().atStartOfDay()
        );

        // Calculate average metrics
        Double avgNPS = calculateAverageNPS(clientId);
        Double avgCSAT = calculateAverageCSAT(clientId);
        Double avgCES = calculateAverageCES(clientId);

        return new DashboardStatsDto(
                totalSurveys,
                activeSurveys,
                totalResponses,
                responsesToday,
                avgNPS,
                avgCSAT,
                avgCES
        );
    }

    public SurveyStatsDto getSurveyStats(Long surveyId) throws BusinessException {
        Long clientId = getCurrentClientId();
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new BusinessException("Pesquisa não encontrada"));

        if (!survey.getClient().getId().equals(clientId)) {
            throw new BusinessException("Acesso negado");
        }

        Integer totalResponses = responseRepository.countBySurveyId(surveyId);
        Optional<LocalDateTime> lastResponse = responseRepository.findLastResponseDateBySurveyId(surveyId);

        List<Question> questions = questionRepository.findBySurveyIdOrderByQuestionOrder(surveyId);
        List<QuestionStatsDto> questionStats = questions.stream()
                .map(this::calculateQuestionStats)
                .collect(Collectors.toList());

        return new SurveyStatsDto(
                surveyId,
                survey.getTitle(),
                totalResponses,
                lastResponse.orElse(null),
                questionStats
        );
    }

    private QuestionStatsDto calculateQuestionStats(Question question) {
        Integer totalAnswers = answerRepository.countByQuestionId(question.getId());
        List<Object[]> distribution = answerRepository.findAnswerDistributionByQuestionId(question.getId());

        Map<String, Integer> answerDistribution = new HashMap<>();
        for (Object[] row : distribution) {
            answerDistribution.put((String) row[0], ((Long) row[1]).intValue());
        }

        List<String> textAnswers = new ArrayList<>();
        Double average = null;
        Integer npsPromoters = null, npsPassives = null, npsDetractors = null;
        Double npsScore = null;

        switch (question.getType()) {
            case TEXT:
                textAnswers = answerRepository.findTextAnswersByQuestionId(question.getId());
                break;
            case NPS:
                npsPromoters = answerRepository.countNpsPromoters(question.getId());
                npsPassives = answerRepository.countNpsPassives(question.getId());
                npsDetractors = answerRepository.countNpsDetractors(question.getId());
                if (totalAnswers > 0) {
                    npsScore = ((double) (npsPromoters - npsDetractors) / totalAnswers) * 100;
                }
                average = answerRepository.findAverageScoreByQuestionId(question.getId()).orElse(null);
                break;
            case CSAT:
            case CES:
                average = answerRepository.findAverageScoreByQuestionId(question.getId()).orElse(null);
                break;
        }

        return new QuestionStatsDto(
                question.getId(),
                question.getText(),
                question.getType().name(),
                totalAnswers,
                answerDistribution,
                textAnswers,
                average,
                npsPromoters,
                npsPassives,
                npsDetractors,
                npsScore
        );
    }

    private Double calculateAverageNPS(Long clientId) {
        List<Survey> surveys = surveyRepository.findByClientIdOrderByCreatedAtDesc(clientId);
        List<Double> npsScores = new ArrayList<>();

        for (Survey survey : surveys) {
            List<Question> npsQuestions = questionRepository.findBySurveyIdAndType(survey.getId(), QuestionType.NPS);
            for (Question question : npsQuestions) {
                Integer totalAnswers = answerRepository.countByQuestionId(question.getId());
                if (totalAnswers > 0) {
                    Integer promoters = answerRepository.countNpsPromoters(question.getId());
                    Integer detractors = answerRepository.countNpsDetractors(question.getId());
                    Double nps = ((double) (promoters - detractors) / totalAnswers) * 100;
                    npsScores.add(nps);
                }
            }
        }

        return npsScores.isEmpty() ? null :
                npsScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private Double calculateAverageCSAT(Long clientId) {
        List<Survey> surveys = surveyRepository.findByClientIdOrderByCreatedAtDesc(clientId);
        List<Double> csatScores = new ArrayList<>();

        for (Survey survey : surveys) {
            List<Question> csatQuestions = questionRepository.findBySurveyIdAndType(survey.getId(), QuestionType.CSAT);
            for (Question question : csatQuestions) {
                Optional<Double> avgScore = answerRepository.findAverageScoreByQuestionId(question.getId());
                avgScore.ifPresent(csatScores::add);
            }
        }

        return csatScores.isEmpty() ? null :
                csatScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private Double calculateAverageCES(Long clientId) {
        List<Survey> surveys = surveyRepository.findByClientIdOrderByCreatedAtDesc(clientId);
        List<Double> cesScores = new ArrayList<>();

        for (Survey survey : surveys) {
            List<Question> cesQuestions = questionRepository.findBySurveyIdAndType(survey.getId(), QuestionType.CES);
            for (Question question : cesQuestions) {
                Optional<Double> avgScore = answerRepository.findAverageScoreByQuestionId(question.getId());
                avgScore.ifPresent(cesScores::add);
            }
        }

        return cesScores.isEmpty() ? null :
                cesScores.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private Long getCurrentClientId() throws BusinessException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return clientRepository.findByEmail(email)
                .map(Client::getId)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
    }
}
