package com.resumematch.dto;

import java.util.List;

public record RequirementAssessmentDto(
        String category,
        String requirement,
        String status,
        String explanation,
        List<String> evidence
) {
}
