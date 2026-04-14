package com.faizan.taskmanager.Client;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class GeminiClient implements LlmClient{

    private final Client client;

    public GeminiClient(@Value("${GEMINI_API_KEY}") String apiKey){
        this.client= Client.builder()
                .apiKey(apiKey)
                .build();
    }


    @Override
    public String generate(String prompt) {
        GenerateContentResponse response =
                client.models.generateContent(
                        "gemini-3-flash-preview",
                        prompt,
                        null
                );
        return response.text();
    }
}
