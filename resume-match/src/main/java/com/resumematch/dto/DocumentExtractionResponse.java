package com.resumematch.dto;

public record DocumentExtractionResponse(
        ResumeProfileDto resumeProfile,
        JobDescriptionProfileDto jobDescriptionProfile
) {
}
