package com.resumematch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumeScreeningErrorResponse {

    private LocalDateTime timestamp;
    private String message;
    private String error;
}
