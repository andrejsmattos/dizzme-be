package com.dizzme.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        // Converte a List<String> para uma String JSON para salvar no banco
        if (attribute == null || attribute.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            // Idealmente, logar o erro
            throw new IllegalArgumentException("Erro ao converter lista para JSON", e);
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        // Converte a String JSON do banco para uma List<String>
        if (dbData == null || dbData.isEmpty()) {
            return null;
        }
        try {
            return objectMapper.readValue(dbData, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            // Idealmente, logar o erro
            throw new IllegalArgumentException("Erro ao converter JSON para lista", e);
        }
    }
}
