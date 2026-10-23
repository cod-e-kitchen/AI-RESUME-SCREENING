package com.resumematch.controller;


import com.resumematch.dto.DocumentExtractionResponse;
import com.resumematch.dto.RequirementAssessmentDto;
import com.resumematch.dto.ResumeMatchResultDto;
import com.resumematch.exception.DocumentExtractionException;
import com.resumematch.service.PdfExtractionService;
import com.resumematch.service.ResumeAnalysisAiService;
import com.resumematch.service.ResumeMatchScoringService;
import com.resumematch.service.ResumeMatchingAiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/resume-api")
public class ResumeScreeningController {

    private final PdfExtractionService pdfExtractionService;
    private final ResumeAnalysisAiService resumeAnalysisAiService;
    private final ResumeMatchingAiService resumeMatchingAiService;
    private final ResumeMatchScoringService resumeMatchScoringService;


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

    @PostMapping(
            value = "/match",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResumeMatchResultDto matchResumeToJob(
            @RequestPart("resume") MultipartFile resume,
            @RequestParam("jobDescription") String jobDescription) {

        DocumentExtractionResponse extraction =
                extractDocument(resume, jobDescription);

        List<RequirementAssessmentDto> assessments =
                resumeMatchingAiService.assessRequirements(extraction);

        return resumeMatchScoringService.calculateScore(
                extraction.jobDescriptionProfile(),
                assessments
        );
    }

    private byte[] getBytes(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException e) {
            throw new DocumentExtractionException("Unable to read uploaded file - "+file.getOriginalFilename());
        }
    }

    private DocumentExtractionResponse extractDocument(
            MultipartFile resume,
            String jobDescription) {

        if (resume == null || resume.isEmpty()) {
            throw new DocumentExtractionException(
                    "Please upload your resume file."
            );
        }

        if (jobDescription == null || jobDescription.isBlank()) {
            throw new DocumentExtractionException(
                    "Job description cannot be empty."
            );
        }

        String contentType = resume.getContentType();

        if (contentType != null
                && !contentType.equalsIgnoreCase("application/pdf")) {
            throw new DocumentExtractionException(
                    "Only PDF files are supported."
            );
        }

        try {
            String resumeText = pdfExtractionService.extractText(
                    resume.getBytes()
            );

            return resumeAnalysisAiService.extractInformation(
                    resumeText,
                    jobDescription
            );

        } catch (IOException ex) {
            throw new DocumentExtractionException(
                    "Unable to read the uploaded resume."
            );
        }
    }


}
