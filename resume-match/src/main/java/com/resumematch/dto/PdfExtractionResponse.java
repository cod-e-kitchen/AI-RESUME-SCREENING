package com.resumematch.dto;

public record PdfExtractionResponse(
        String fileName,
        int extractedCharacterCount,
        String textPreview
) {
}
