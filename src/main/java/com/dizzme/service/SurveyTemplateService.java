package com.dizzme.service;

import com.dizzme.dto.QuestionTemplate;
import com.dizzme.dto.SurveyTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SurveyTemplateService {

    public List<SurveyTemplate> getTemplates() {
        List<SurveyTemplate> templates = new ArrayList<>();

        // NPS Template
        templates.add(new SurveyTemplate(
                "Net Promoter Score (NPS)",
                "Modelo padrão para pesquisa NPS",
                "satisfaction",
                Arrays.asList(
                        new QuestionTemplate("RATING", "Em uma escala de 0 a 10, o quanto você recomendaria nossa empresa/produto/serviço para um amigo ou colega?", null, true, 1),
                        new QuestionTemplate("TEXT", "O que é mais importante para melhorar sua avaliação?", null, false, 2)
                )
        ));

        // CSAT Template
        templates.add(new SurveyTemplate(
                "Customer Satisfaction (CSAT)",
                "Modelo para pesquisa de satisfação do cliente",
                "satisfaction",
                Arrays.asList(
                        new QuestionTemplate("RATING", "Como você avalia sua satisfação geral com nosso atendimento?", null, true, 1),
                        new QuestionTemplate("TEXT", "Deixe seus comentários ou sugestões:", null, false, 2)
                )
        ));

        // CES Template
        templates.add(new SurveyTemplate(
                "Customer Effort Score (CES)",
                "Modelo para medir facilidade de uso/atendimento",
                "effort",
                Arrays.asList(
                        new QuestionTemplate("RATING", "O quanto foi fácil resolver seu problema hoje?", null, true, 1),
                        new QuestionTemplate("MULTIPLE_CHOICE", "Qual canal você utilizou?",
                                "[\"Telefone\", \"Email\", \"Chat\", \"WhatsApp\", \"Presencial\"]", true, 2)
                )
        ));

        return templates;
    }
}
