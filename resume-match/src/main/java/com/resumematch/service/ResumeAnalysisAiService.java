package com.resumematch.service;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.resumematch.dto.DocumentExtractionResponse;
import com.resumematch.exception.AiModelChatException;
import com.resumematch.exception.DocumentExtractionException;
import com.resumematch.util.AiContextPromptUtil;
import dev.langchain4j.model.chat.ChatModel;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class ResumeAnalysisAiService {

    private final ChatModel chatModel;
    private final ObjectMapper objectMapper;

    public DocumentExtractionResponse extractInformation(String resumeText, String jobDescription) {

        log.info("Inside extractInformation method");

        if(resumeText == null || resumeText.isEmpty() ) {
            log.info("resumeText is null or empty");
            throw new DocumentExtractionException("Resume text cannot be empty.");
        }

        if( jobDescription == null || jobDescription.isEmpty()){
            log.info("jobDescription is null or empty");
            throw new DocumentExtractionException("Job description cannot be empty.");
        }

        String prompt = buildPrompt(resumeText, jobDescription);
        log.info("Prompt built with character count: {}", prompt.length());

        try {
            String response = chatModel.chat(prompt);
            log.info("response returned by model with char count: {}", response.length());

            if(response == null || response.isBlank()) {
                log.info("response is null or empty");
                throw new AiModelChatException("Ai model was unable to return a response.");
            }

            String json = response.trim();
//            log.info("json: {}", json);

            // Handle models that wrap JSON in Markdown fences.
            if (json.startsWith("```")) {
                log.info("Inside JSON in Markdown fences handling");
                json = json.replaceFirst(
                        "^```(?:json)?\\s*", ""
                );
                json = json.replaceFirst("\\s*```$", "");
                log.info("json after markdown handling completed.");
            }

            DocumentExtractionResponse result =
                    objectMapper.readValue(
                            json,
                            DocumentExtractionResponse.class
                    );
//            log.info("result: {}", result);

            if (result.resumeProfile() == null
                    || result.jobDescriptionProfile() == null) {
                log.info("result is null or empty");
                throw new AiModelChatException("The AI response is missing required sections.");
            }
            return result;
        }catch (JsonProcessingException ex){
            throw new AiModelChatException("The AI model returned an invalid JSON response.");
        }

    }

    private String buildPrompt(String resumeText, String jobDescription) {
//        AiContextPromptUtil aiContextPromptUtil = new AiContextPromptUtil();
        return AiContextPromptUtil.EXTRACTION_PROMPT.formatted(resumeText, jobDescription);
    }
}
