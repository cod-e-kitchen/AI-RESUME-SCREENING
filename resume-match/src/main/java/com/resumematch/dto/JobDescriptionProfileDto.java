
package com.resumematch.dto;

import java.util.List;

public record JobDescriptionProfileDto(
        List<String> requiredSkills,
        List<String> preferredSkills,
        List<String> responsibilities,
        List<String> qualifications
) {
}
