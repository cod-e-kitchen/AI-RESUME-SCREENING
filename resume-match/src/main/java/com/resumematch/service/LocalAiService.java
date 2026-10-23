package com.resumematch.service;

import dev.langchain4j.model.chat.ChatModel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LocalAiService {

    private final ChatModel chatModel;

    public String testConnection() {
        String prompt = """
                You are an assistant testing an AI integration.
                
                Return a short response confirming that you can help
                analyze resumes against job descriptions.
                
                Do not provide a resume score.
                """;

        return chatModel.chat(prompt);
    }

}
