package com.dizzme.service;

import com.dizzme.dto.*;
import com.dizzme.entity.*;
import com.dizzme.exception.BusinessException;
import com.dizzme.repository.ClientRepository;
import com.dizzme.repository.QuestionRepository;
import com.dizzme.repository.ResponseRepository;
import com.dizzme.repository.SurveyRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class SurveyService {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private QRCodeService qrCodeService;

    @Autowired
    private ObjectMapper objectMapper;

    @Transactional
    public SurveyDto createSurvey(SurveyCreateRequest request) throws BusinessException {
        Long clientId = getCurrentClientId();
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));

        String publicId = generatePublicId();

        Survey survey = new Survey(client, request.title(), request.description(), publicId);
        survey = surveyRepository.save(survey);

        // Create questions
        List<Question> questions = new ArrayList<>();
        for (QuestionCreateRequest qReq : request.questions()) {
            Question question = new Question(
                    survey,
                    QuestionType.valueOf(qReq.type().toUpperCase()),
                    qReq.text(),
                    qReq.questionOrder()
            );
            question.setOptions(qReq.options());
            question.setRequired(qReq.required() != null ? qReq.required() : true);
            questions.add(question);
        }

        questionRepository.saveAll(questions);
        survey.setQuestions(questions);

        return mapToDto(survey);
    }

    public List<SurveySummaryDto> getMySurveys() {
        Long clientId = getCurrentClientId();
        List<Survey> surveys = surveyRepository.findByClientIdOrderByCreatedAtDesc(clientId);

        return surveys.stream()
                .map(this::mapToSummaryDto)
                .collect(Collectors.toList());
    }

    public SurveyDto getSurvey(Long id) throws BusinessException {
        Long clientId = getCurrentClientId();
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Pesquisa não encontrada"));

        if (!survey.getClient().getId().equals(clientId)) {
            throw new BusinessException("Acesso negado");
        }

        return mapToDto(survey);
    }

    public SurveyDto getPublicSurvey(String publicId) throws BusinessException {
        Survey survey = surveyRepository.findByPublicId(publicId)
                .orElseThrow(() -> new BusinessException("Pesquisa não encontrada"));

        if (!survey.getActive() || survey.getStatus() != SurveyStatus.PUBLISHED) {
            throw new BusinessException("Pesquisa não disponível");
        }

        return mapToDto(survey);
    }

    @Transactional
    public SurveyDto updateSurvey(Long id, SurveyUpdateRequest request) throws BusinessException {
        Long clientId = getCurrentClientId();
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Pesquisa não encontrada"));

        if (!survey.getClient().getId().equals(clientId)) {
            throw new BusinessException("Acesso negado");
        }

        if (request.title() != null) {
            survey.setTitle(request.title());
        }

        if (request.description() != null) {
            survey.setDescription(request.description());
        }

        if (request.active() != null) {
            survey.setActive(request.active());
        }

        if (request.status() != null) {
            survey.setStatus(SurveyStatus.valueOf(request.status().toUpperCase()));
        }

        // Update questions if provided
        if (request.questions() != null) {
            questionRepository.deleteBySurveyId(id);

            List<Question> questions = new ArrayList<>();
            for (QuestionCreateRequest qReq : request.questions()) {
                Question question = new Question(
                        survey,
                        QuestionType.valueOf(qReq.type().toUpperCase()),
                        qReq.text(),
                        qReq.questionOrder()
                );
                question.setOptions(qReq.options());
                question.setRequired(qReq.required() != null ? qReq.required() : true);
                questions.add(question);
            }

            questionRepository.saveAll(questions);
        }

        survey = surveyRepository.save(survey);
        return mapToDto(survey);
    }

    @Transactional
    public void deleteSurvey(Long id) throws BusinessException {
        Long clientId = getCurrentClientId();
        Survey survey = surveyRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Pesquisa não encontrada"));

        if (!survey.getClient().getId().equals(clientId)) {
            throw new BusinessException("Acesso negado");
        }

        surveyRepository.delete(survey);
    }

    private SurveyDto mapToDto(Survey survey) {
        List<QuestionDto> questionDtos = survey.getQuestions() != null ?
                survey.getQuestions().stream().map(this::mapQuestionToDto).collect(Collectors.toList()) :
                new ArrayList<>();

        Integer responsesCount = responseRepository.countBySurveyId(survey.getId());
        String publicUrl = "http://localhost:4200/survey/" + survey.getPublicId();
        String qrCodeUrl = "/api/qr/generate?url=" + publicUrl;

        return new SurveyDto(
                survey.getId(),
                survey.getTitle(),
                survey.getDescription(),
                survey.getPublicId(),
                survey.getActive(),
                survey.getStatus().name(),
                survey.getCreatedAt(),
                survey.getUpdatedAt(),
                questionDtos,
                responsesCount,
                publicUrl,
                qrCodeUrl
        );
    }

    private SurveySummaryDto mapToSummaryDto(Survey survey) {
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

    private QuestionDto mapQuestionToDto(Question question) {
        return new QuestionDto(
                question.getId(),
                question.getType().name(),
                question.getText(),
                question.getOptions(),
                question.getQuestionOrder(),
                question.getRequired()
        );
    }

    private String generatePublicId() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 12);
    }

    private Long getCurrentClientId() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return clientRepository.findByEmail(email)
                .map(Client::getId)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
    }
}