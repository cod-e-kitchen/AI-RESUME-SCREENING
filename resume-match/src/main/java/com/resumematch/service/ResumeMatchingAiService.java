package com.resumematch.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumematch.dto.DocumentExtractionResponse;
import com.resumematch.dto.RequirementAssessmentDto;
import com.resumematch.exception.AiModelChatException;
import com.resumematch.util.AiContextPromptUtil;
import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class ResumeMatchingAiService {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    public List<RequirementAssessmentDto> assessRequirements(DocumentExtractionResponse extraction){

        log.info("assessRequirements start");

        if(extraction == null || extraction.resumeProfile() == null
                || extraction.jobDescriptionProfile() == null){
            log.info("assessRequirements --> Resume or JD is null.");
            throw new AiModelChatException("Resume and Job Description Profile Required");
        }

        try{
            String resumeJson =  objectMapper.writeValueAsString(extraction.resumeProfile());
            log.info("assessRequirements --> resumeJson char count: {}", resumeJson.length());

            String jobDescriptionJson =  objectMapper.writeValueAsString(extraction.jobDescriptionProfile());
            log.info("assessRequirements --> jobDescriptionJson char count: {}", jobDescriptionJson.length());

            String prompt = buildPrompt(jobDescriptionJson, resumeJson);
            log.info("assessRequirements --> prompt char count: {}", prompt.length());

            log.info("assessRequirements --> Sending prompt to model.");
            String response = chatModel.chat(prompt);
            log.info("response returned by model with char count: {}", response.length());

            if(response == null || response.isBlank()){
                log.info("assessRequirements --> AI model returned null response");
                throw new AiModelChatException("Ai model was unable to process the request.");
            }

            String json = removeMarkdownFences(response);
            MatchingAiResponse result = objectMapper.readValue(json, MatchingAiResponse.class);

            if(result.assessments == null){
                log.info("assessRequirements --> No assessments found.");
                throw new AiModelChatException("Ai response is missing required assessments.");
            }

            log.info("assessRequirements --> END");

            return result.assessments();


        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildPrompt(String resumeJson, String jobDescriptionJson){
        log.info("buildPrompt start");
        return AiContextPromptUtil.EVIDENCE_PROMPT.formatted(resumeJson, jobDescriptionJson);
    }

    private String removeMarkdownFences(String response) {
        log.info("removeMarkdownFences start");
        if (response.startsWith("```")) {
            response = response.replaceFirst(
                    "^```(?:json)?\\s*", ""
            );
            response = response.replaceFirst("\\s*```$", "");
        }

        return response;
    }

    private record MatchingAiResponse(
            List<RequirementAssessmentDto> assessments) {
    }
}
