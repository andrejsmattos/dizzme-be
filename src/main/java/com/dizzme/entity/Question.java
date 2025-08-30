package com.dizzme.entity;

import com.dizzme.config.StringListConverter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List; // IMPORTANTE: Importe a classe List

@Entity
@Table(name = "questions")
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    @JsonBackReference
    private Survey survey;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Texto da pergunta é obrigatório")
    private String text;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = StringListConverter.class)
    private List<String> options;

    @Column(nullable = false)
    private Integer questionOrder;

    @Column(nullable = false)
    private Boolean required = true;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Answer> answers;

    // Constructors
    public Question() {}

    public Question(Survey survey, QuestionType type, String text, Integer questionOrder) {
        this.survey = survey;
        this.type = type;
        this.text = text;
        this.questionOrder = questionOrder;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Survey getSurvey() { return survey; }
    public void setSurvey(Survey survey) { this.survey = survey; }

    public QuestionType getType() { return type; }
    public void setType(QuestionType type) { this.type = type; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }

    public Integer getQuestionOrder() { return questionOrder; }
    public void setQuestionOrder(Integer questionOrder) { this.questionOrder = questionOrder; }

    public Boolean getRequired() { return required; }
    public void setRequired(Boolean required) { this.required = required; }

    public List<Answer> getAnswers() { return answers; }
    public void setAnswers(List<Answer> answers) { this.answers = answers; }
}
