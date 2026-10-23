//package com.resumematch.config;
//
//
//import dev.langchain4j.model.chat.ChatModel;
//import dev.langchain4j.model.ollama.OllamaChatModel;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import java.time.Duration;
//
//@Configuration
//public class OllamaConfig {
//
//    @Bean
//    public ChatModel chatModel(@Value("${resumematch.ai.base-url}") String baseUrl,
//                               @Value("${resumematch.ai.model-name}") String modelName,
//                               @Value("${resumematch.ai.timeout-seconds}") long timeoutSeconds) {
//        return OllamaChatModel.builder()
//                .baseUrl(baseUrl)
//                .modelName(modelName)
//                .temperature(0.0)
//                .timeout(Duration.ofSeconds(timeoutSeconds))
//                .build();
//    }
//}
