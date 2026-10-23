package com.resumematch.controller;


import com.resumematch.dto.DocumentExtractionResponse;
import com.resumematch.dto.PdfExtractionResponse;
import com.resumematch.exception.DocumentExtractionException;
import com.resumematch.service.PdfExtractionService;
import com.resumematch.service.ResumeAnalysisAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resume-api")
public class ResumeScreeningController {

    private final PdfExtractionService pdfExtractionService;
    private final ResumeAnalysisAiService resumeAnalysisAiService;

    @PostMapping(value = "/pdf-extraction", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public DocumentExtractionResponse extractTextFromPDF(
            @RequestPart("resume")MultipartFile resume,
            @RequestParam("jobDescription") String jobDescription){

        if(resume == null || resume.isEmpty()){
            throw new DocumentExtractionException("Please upload your resume file");
        }
        if (jobDescription == null || jobDescription.isBlank()) {
            throw new DocumentExtractionException("Job description cannot be empty.");
        }

        String resumeContentType = resume.getContentType();
        if( resumeContentType != null
                && !resumeContentType.equalsIgnoreCase("application/pdf")){
            throw new DocumentExtractionException("Only PDF files are supported");
        }

        String extractedText = pdfExtractionService.extractText(getBytes(resume));
//        int previewLength = Math.min(300, extractedText.length());

        return resumeAnalysisAiService.extractInformation(extractedText,jobDescription);

//            return new PdfExtractionResponse(resume.getOriginalFilename(),
//                    extractedText.length(),
//                    extractedText.substring(0, previewLength));


    }

    private byte[] getBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new DocumentExtractionException("Unable to read uploaded file - "+file.getOriginalFilename());
        }
    }


}
