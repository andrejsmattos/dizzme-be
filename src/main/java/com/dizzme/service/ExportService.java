package com.dizzme.service;

import com.dizzme.dto.ExportRequest;
import com.dizzme.entity.Question;
import com.dizzme.entity.Response;
import com.dizzme.entity.Survey;
import com.dizzme.entity.Client;
import com.dizzme.entity.Answer;
import com.dizzme.exception.BusinessException;
import com.dizzme.repository.ClientRepository;
import com.dizzme.repository.QuestionRepository;
import com.dizzme.repository.ResponseRepository;
import com.dizzme.repository.SurveyRepository;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExportService {

    @Autowired
    private SurveyRepository surveyRepository;

    @Autowired
    private ResponseRepository responseRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ClientRepository clientRepository;

    public byte[] exportSurveyData(ExportRequest request) throws BusinessException {
        Long clientId = getCurrentClientId();
        Survey survey = surveyRepository.findById(request.surveyId())
                .orElseThrow(() -> new BusinessException("Pesquisa não encontrada"));

        if (!survey.getClient().getId().equals(clientId)) {
            throw new BusinessException("Acesso negado");
        }

        List<Response> responses;
        if (request.dateFrom() != null && request.dateTo() != null) {
            responses = responseRepository.findBySurveyIdAndDateRange(
                    request.surveyId(), request.dateFrom(), request.dateTo());
        } else {
            responses = responseRepository.findBySurveyIdOrderBySubmittedAtDesc(request.surveyId());
        }

        List<Question> questions = questionRepository.findBySurveyIdOrderByQuestionOrder(request.surveyId());

        if ("xlsx".equalsIgnoreCase(request.format())) {
            return exportToExcel(survey, questions, responses, request.includeHeaders());
        } else {
            return exportToCsv(survey, questions, responses, request.includeHeaders());
        }
    }

    private byte[] exportToCsv(Survey survey, List<Question> questions,
                               List<Response> responses, Boolean includeHeaders) throws BusinessException {
        try {
            java.io.StringWriter writer = new java.io.StringWriter();
            com.opencsv.CSVWriter csvWriter = new com.opencsv.CSVWriter(writer);

            // Headers
            if (includeHeaders == null || includeHeaders) {
                List<String> headers = new ArrayList<>();
                headers.add("ID da Resposta");
                headers.add("Data de Envio");
                headers.add("IP do Usuário");

                for (Question question : questions) {
                    headers.add(question.getText());
                }

                csvWriter.writeNext(headers.toArray(new String[0]));
            }

            // Data rows
            for (Response response : responses) {
                List<String> row = new ArrayList<>();
                row.add(response.getId().toString());
                row.add(response.getSubmittedAt().toString());
                row.add(response.getUserIp());

                for (Question question : questions) {
                    String answerValue = response.getAnswers().stream()
                            .filter(a -> a.getQuestion().getId().equals(question.getId()))
                            .map(Answer::getValue)
                            .findFirst()
                            .orElse("");
                    row.add(answerValue);
                }

                csvWriter.writeNext(row.toArray(new String[0]));
            }

            csvWriter.close();
            return writer.toString().getBytes("UTF-8");

        } catch (Exception e) {
            throw new BusinessException("Erro ao exportar CSV: " + e.getMessage());
        }
    }

    private byte[] exportToExcel(Survey survey, List<Question> questions,
                                 List<Response> responses, Boolean includeHeaders) throws BusinessException {
        try {
            XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
            XSSFSheet sheet = workbook.createSheet("Respostas");

            int rowNum = 0;

            // Headers
            if (includeHeaders == null || includeHeaders) {
                org.apache.poi.xssf.usermodel.XSSFRow headerRow = sheet.createRow(rowNum++);
                int cellNum = 0;

                headerRow.createCell(cellNum++).setCellValue("ID da Resposta");
                headerRow.createCell(cellNum++).setCellValue("Data de Envio");
                headerRow.createCell(cellNum++).setCellValue("IP do Usuário");

                for (Question question : questions) {
                    headerRow.createCell(cellNum++).setCellValue(question.getText());
                }
            }

            // Data rows
            for (Response response : responses) {
                XSSFRow row = sheet.createRow(rowNum++);
                int cellNum = 0;

                row.createCell(cellNum++).setCellValue(response.getId());
                row.createCell(cellNum++).setCellValue(response.getSubmittedAt().toString());
                row.createCell(cellNum++).setCellValue(response.getUserIp());

                for (Question question : questions) {
                    String answerValue = response.getAnswers().stream()
                            .filter(a -> a.getQuestion().getId().equals(question.getId()))
                            .map(Answer::getValue)
                            .findFirst()
                            .orElse("");
                    row.createCell(cellNum++).setCellValue(answerValue);
                }
            }

            java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
            workbook.write(baos);
            workbook.close();

            return baos.toByteArray();

        } catch (Exception e) {
            throw new BusinessException("Erro ao exportar Excel: " + e.getMessage());
        }
    }

    private Long getCurrentClientId() throws BusinessException {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return clientRepository.findByEmail(email)
                .map(Client::getId)
                .orElseThrow(() -> new BusinessException("Cliente não encontrado"));
    }
}
