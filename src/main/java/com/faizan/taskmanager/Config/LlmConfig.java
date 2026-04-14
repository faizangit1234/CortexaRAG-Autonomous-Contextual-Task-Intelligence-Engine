package com.faizan.taskmanager.Config;

import com.faizan.taskmanager.Client.GeminiClient;
import com.faizan.taskmanager.Client.LlmClient;
import com.faizan.taskmanager.Client.OllamaClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LlmConfig {

    @Bean
    public LlmClient llmClient(
            @Value("${LLM.PROVIDER}") String provider,
            OllamaClient ollamaClient,
            GeminiClient geminiClient
    ) {
        return switch (provider.toLowerCase()){
            case "gemini" -> geminiClient;
            case "ollama" -> ollamaClient;
            default -> throw new IllegalArgumentException("Unknown Provider");
        };
    }
}
