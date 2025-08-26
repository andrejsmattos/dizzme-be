package com.dizzme.entity;

public enum QuestionType {
    MULTIPLE_CHOICE, // Múltipla escolha
    NPS,             // Net Promoter Score (0-10)
    CSAT,            // Customer Satisfaction (1-5)
    CES,             // Customer Effort Score (1-7)
    EMOTION,         // Escala de emoções
    LIKE_DISLIKE,    // Like/Dislike
    TEXT,            // Texto livre
    DROPDOWN,        // Lista suspensa
    RADIO            // Radio button
}