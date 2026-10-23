
package com.resumematch.dto;

import java.util.List;

public record ResumeProfileDto(
        List<String> skills,
        List<String> qualifications,
        String experienceSummary,
        List<EvidenceDto> evidence
) {
}
