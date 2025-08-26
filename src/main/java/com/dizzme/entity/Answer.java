package com.dizzme.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "answers")
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "response_id", nullable = false)
    @JsonBackReference
    private Response response;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonBackReference
    private Question question;

    @Column(columnDefinition = "TEXT")
    private String value; // JSON or text value

    // Constructors
    public Answer() {}

    public Answer(Response response, Question question, String value) {
        this.response = response;
        this.question = question;
        this.value = value;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Response getResponse() { return response; }
    public void setResponse(Response response) { this.response = response; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }

    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
