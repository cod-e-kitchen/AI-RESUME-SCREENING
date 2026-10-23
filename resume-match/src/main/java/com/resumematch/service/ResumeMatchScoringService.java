package com.resumematch.service;

import com.resumematch.dto.JobDescriptionProfileDto;
import com.resumematch.dto.RequirementAssessmentDto;
import com.resumematch.dto.ResumeMatchResultDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.resumematch.util.AppConstants.*;

@Service
@Slf4j
public class ResumeMatchScoringService {

    private final double requiredWeight;
    private final double preferredWeight;
    private final double directMatchScore;
    private final double partialMatchScore;
    private final double noEvidenceScore;

    public ResumeMatchScoringService(
            @Value("${resumematch.matching.required-weight:0.80}")
            double requiredWeight,
            @Value("${resumematch.matching.preferred-weight:0.20}")
            double preferredWeight,
            @Value("${resumematch.matching.direct-match-score:1.0}")
            double directMatchScore,
            @Value("${resumematch.matching.partial-match-score:0.5}")
            double partialMatchScore,
            @Value("${resumematch.matching.no-evidence-score:0.0}")
            double noEvidenceScore) {

        if (requiredWeight < 0 || preferredWeight < 0
                || requiredWeight + preferredWeight <= 0) {
            throw new IllegalArgumentException(
                    "Matching weights must be non-negative and their sum must be positive."
            );
        }

        if (directMatchScore < 0 || directMatchScore > 1
                || partialMatchScore < 0 || partialMatchScore > 1
                || noEvidenceScore < 0 || noEvidenceScore > 1) {
            throw new IllegalArgumentException(
                    "Matching status scores must be between 0 and 1."
            );
        }

        this.requiredWeight = requiredWeight;
        this.preferredWeight = preferredWeight;
        this.directMatchScore = directMatchScore;
        this.partialMatchScore = partialMatchScore;
        this.noEvidenceScore = noEvidenceScore;
    }

    public ResumeMatchResultDto calculateScore(
            JobDescriptionProfileDto jobProfile,
            List<RequirementAssessmentDto> assessments) {

        log.info("calculateScore start");

        double requiredScore = categoryScore(
                assessments, REQUIRED_SKILL
        );

        double preferredScore = categoryScore(
                assessments, PREFERRED_SKILL
        );

        boolean hasRequiredSkills = hasAssessments(
                assessments, REQUIRED_SKILL
        );

        boolean hasPreferredSkills = hasAssessments(
                assessments, PREFERRED_SKILL
        );

        double activeWeight = 0;
        double weightedScore = 0;

        if (hasRequiredSkills) {
            activeWeight += requiredWeight;
            weightedScore += requiredScore * requiredWeight;
        }

        if (hasPreferredSkills) {
            activeWeight += preferredWeight;
            weightedScore += preferredScore * preferredWeight;
        }

        double overallScore = activeWeight == 0
                ? 0
                : weightedScore / activeWeight;

        List<String> missingRequiredSkills =
                assessments.stream()
                        .filter(a -> REQUIRED_SKILL.equals(a.category()))
                        .filter(a -> PARTIAL_MATCH.equals(a.status())
                                || NO_EVIDENCE.equals(a.status()))
                        .map(RequirementAssessmentDto::requirement)
                        .distinct()
                        .toList();

        log.info("calculateScore end");

        return new ResumeMatchResultDto(
                round(overallScore),
                round(requiredScore),
                round(preferredScore),
                assessments,
                missingRequiredSkills
        );
    }

    private double categoryScore(
            List<RequirementAssessmentDto> assessments,
            String category) {

        List<RequirementAssessmentDto> applicable = assessments.stream()
                .filter(a -> category.equals(a.category()))
                .filter(a -> !NOT_ASSESSABLE.equals(a.status()))
                .toList();

        if (applicable.isEmpty()) {
            return 0;
        }

        double total = applicable.stream()
                .mapToDouble(a -> scoreForStatus(a.status()))
                .sum();

        return (total / applicable.size()) * 100;
    }

    private boolean hasAssessments(
            List<RequirementAssessmentDto> assessments,
            String category) {

        return assessments.stream()
                .anyMatch(a -> category.equals(a.category()));
    }

    private double scoreForStatus(String status) {
        return switch (status) {
            case DIRECT_MATCH -> directMatchScore;
            case PARTIAL_MATCH -> partialMatchScore;
            case NO_EVIDENCE -> noEvidenceScore;
            case NOT_ASSESSABLE -> 0;
            default -> throw new IllegalArgumentException(
                    "Unknown matching status: " + status
            );
        };
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}
