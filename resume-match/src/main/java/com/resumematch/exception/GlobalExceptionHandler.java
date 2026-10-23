package com.resumematch.exception;

import com.resumematch.dto.ResumeScreeningErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DocumentExtractionException.class)
    public ResponseEntity<?> handleDocumentExtractionException(DocumentExtractionException e){
        ResumeScreeningErrorResponse errorResponse = new ResumeScreeningErrorResponse(
                LocalDateTime.now(), e.getMessage(), "Document Processing failed."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DocumentSecurityException.class)
    public ResponseEntity<?> handleDocumentSecurityException(DocumentSecurityException e){
        ResumeScreeningErrorResponse errorResponse = new ResumeScreeningErrorResponse(
                LocalDateTime.now(), e.getMessage(), "Document Unauthorized to process."
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception e){
        ResumeScreeningErrorResponse errorResponse = new ResumeScreeningErrorResponse(LocalDateTime.now(), e.getMessage(), "Unable to process request.");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AiModelChatException.class)
    public ResponseEntity<?> handleAiModelChatException(AiModelChatException e){
        ResumeScreeningErrorResponse errorResponse = new ResumeScreeningErrorResponse(LocalDateTime.now(), e.getMessage(), "AI Model Unable to process request.");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(ResumeMatchServiceException.class)
    public ResponseEntity<?> handleResumeMatchServiceException(ResumeMatchServiceException e){
        ResumeScreeningErrorResponse errorResponse = new ResumeScreeningErrorResponse(LocalDateTime.now(), e.getMessage(), "Unable to match resume with the job.");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e){
        ResumeScreeningErrorResponse errorResponse = new ResumeScreeningErrorResponse(LocalDateTime.now(), e.getMessage(), "Unable to process request.");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }


}
