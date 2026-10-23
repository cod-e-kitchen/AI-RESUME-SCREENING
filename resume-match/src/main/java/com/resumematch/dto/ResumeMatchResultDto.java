package com.resumematch.dto;

import java.util.List;

public record ResumeMatchResultDto(
        double overallScore,
        double requiredSkillsScore,
        double preferredSkillsScore,
        List<RequirementAssessmentDto> assessments,
        List<String> missingRequiredSkills
) {
}
