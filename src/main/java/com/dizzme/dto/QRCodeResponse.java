package com.dizzme.dto;

public record QRCodeResponse(
        String base64Image,
        String url,
        Integer size
) {}