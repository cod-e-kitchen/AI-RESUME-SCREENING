package com.resumematch.config;


import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.anthropic.AnthropicChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class AnthropicConfig {

    @Bean
    public ChatModel chatModel(
            @Value("${resumematch.ai.api-key}") String apiKey,
            @Value("${resumematch.ai.model-name}") String modelName,
            @Value("${resumematch.ai.timeout-seconds}") long timeoutSeconds) {

        return AnthropicChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                /* Uncomment temperature if not using haikyu model */
//                .temperature(0.0)
                .timeout(Duration.ofSeconds(timeoutSeconds))
                .maxTokens(8192)
                .build();
    }
}
