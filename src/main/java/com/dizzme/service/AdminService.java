package com.dizzme.service;

import com.dizzme.dto.AdminDashboardDto;
import com.dizzme.dto.ClientSummaryDto;
import com.dizzme.dto.SurveySummaryDto;
import com.dizzme.entity.Client;
import com.dizzme.entity.Survey;
import com.dizzme.repository.ClientRepository;
import com.dizzme.repository.QuestionRepository;
import com.dizzme.repository.ResponseRepository;
import com.dizzme.repository.SurveyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AdminService {

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private QuestionRepository questionRepository;

    public AdminDashboardDto getAdminDashboard() {
        Integer totalClients = Math.toIntExact(clientRepository.count());
        Integer activeClients = clientRepository.countActiveClients();
        Integer totalSurveys = Math.toIntExact(surveyRepository.count());
        Integer totalResponses = Math.toIntExact(responseRepository.count());

        // Monthly stats (last 6 months)
        Map<String, Integer> monthlyStats = new HashMap<>();

        // Recent clients (last 10)
        List<Client> recentClients = clientRepository.findRecentClients(
                LocalDateTime.now().minusDays(30));
        List<ClientSummaryDto> recentClientsDto = recentClients.stream()
                .map(this::mapToClientSummary)
                .collect(Collectors.toList());

        // Recent surveys
        List<Survey> recentSurveys = surveyRepository.findRecentSurveys(
                LocalDateTime.now().minusDays(7));
        List<SurveySummaryDto> recentSurveysDto = recentSurveys.stream()
                .map(this::mapToSurveySummary)
                .collect(Collectors.toList());

        return new AdminDashboardDto(
                totalClients,
                activeClients,
                totalSurveys,
                totalResponses,
                monthlyStats,
                recentClientsDto,
                recentSurveysDto
        );
    }

    private ClientSummaryDto mapToClientSummary(Client client) {
        Integer surveysCount = surveyRepository.countByClientId(client.getId());
        Integer responsesCount = responseRepository.countByClientId(client.getId());

        return new ClientSummaryDto(
                client.getId(),
                client.getName(),
                client.getEmail(),
                client.getActive(),
                client.getCreatedAt(),
                surveysCount,
                responsesCount
        );
    }

    private SurveySummaryDto mapToSurveySummary(Survey survey) {
        Integer questionsCount = questionRepository.countBySurveyId(survey.getId());
        Integer responsesCount = responseRepository.countBySurveyId(survey.getId());
        String publicUrl = "http://localhost:4200/survey/" + survey.getPublicId();

        return new SurveySummaryDto(
                survey.getId(),
                survey.getTitle(),
                survey.getDescription(),
                survey.getActive(),
                survey.getStatus().name(),
                survey.getCreatedAt(),
                questionsCount,
                responsesCount,
                publicUrl
        );
    }

}
