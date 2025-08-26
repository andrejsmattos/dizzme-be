package com.dizzme.service;

import com.dizzme.dto.AnswerDto;
import com.dizzme.dto.AnswerSubmitRequest;
import com.dizzme.dto.ResponseDto;
import com.dizzme.dto.ResponseSubmitRequest;
import com.dizzme.entity.*;
import com.dizzme.exception.BusinessException;
import com.dizzme.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ResponseService {

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private AnswerRepository answerRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Transactional
    public void submitResponse(ResponseSubmitRequest request, String userIp, String userAgent) throws BusinessException {
        Survey survey = surveyRepository.findByPublicId(request.surveyPublicId())
                .orElseThrow(() -> new BusinessException("Pesquisa não encontrada"));

        if (!survey.getActive() || survey.getStatus() != SurveyStatus.PUBLISHED) {
            throw new BusinessException("Pesquisa não disponível para respostas");
        }

        if (request.lgpdConsent() == null || !request.lgpdConsent()) {
            throw new BusinessException("Consentimento LGPD é obrigatório");
        }

        Response response = new Response(survey, userIp, userAgent);
        response = responseRepository.save(response);

        // Save answers
        List<Answer> answers = new ArrayList<>();
        for (AnswerSubmitRequest answerReq : request.answers()) {
            Question question = questionRepository.findById(answerReq.questionId())
                    .orElseThrow(() -> new BusinessException("Pergunta não encontrada"));

            if (!question.getSurvey().getId().equals(survey.getId())) {
                throw new BusinessException("Pergunta não pertence à pesquisa");
            }

            Answer answer = new Answer(response, question, answerReq.value());
            answers.add(answer);
        }

        answerRepository.saveAll(answers);
    }

    public List<ResponseDto> getSurveyResponses(Long surveyId) throws BusinessException {
        Long clientId = getCurrentClientId();
        Survey survey = surveyRepository.findById(surveyId)
                .orElseThrow(() -> new BusinessException("Pesquisa não encontrada"));

        if (!survey.getClient().getId().equals(clientId)) {
            throw new BusinessException("Acesso negado");
        }

        List<Response> responses = responseRepository.findBySurveyIdOrderBySubmittedAtDesc(surveyId);

        return responses.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private ResponseDto mapToDto(Response response) {
        List<AnswerDto> answerDtos = response.getAnswers().stream()
                .map(this::mapAnswerToDto)
                .collect(Collectors.toList());

        return new ResponseDto(
                response.getId(),
                response.getSubmittedAt(),
                response.getUserIp(),
                answerDtos
        );
    }

    private AnswerDto mapAnswerToDto(Answer answer) {
        return new AnswerDto(
                answer.getId(),
                answer.getQuestion().getId(),
                answer.getQuestion().getText(),
                answer.getQuestion().getType().name(),
                answer.getValue()
        );
    }

    private Long getCurrentClientId() throws BusinessException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Client client = clientRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
        return client.getId();
    }

}
