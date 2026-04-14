package com.faizan.taskmanager.Client;

import com.faizan.taskmanager.Dto.*;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpHeaders;

import java.util.List;


@Service
public class OllamaClient implements LlmClient{
    private final RestTemplate restTemplate;
    private static final String EMBEDDING_URL="http://localhost:11434/api/embeddings";

    public OllamaClient(RestTemplate restTemplate){
        this.restTemplate = restTemplate;
    }

    public List<Double> generateEmbedding(String text){
        EmbeddingRequest request= new EmbeddingRequest("nomic-embed-text",text);

        ResponseEntity<EmbeddingResponse> response = restTemplate.postForEntity(EMBEDDING_URL,request,EmbeddingResponse.class);
        if(response.getBody()==null || response.getBody().getEmbedding()==null){
            throw new RuntimeException("Failed to generate embeddings");
        }
        return response.getBody().getEmbedding();
    }

    @Override
    public String generate(String prompt){
        String url = "http://localhost:11434/api/chat";

        AIRequest request = new AIRequest();
        request.setModel("gemma:2b");
        request.setStream(false);

        AIRequest.Message msg = new AIRequest.Message();
        msg.setRole("user");
        msg.setContent(prompt);

        request.setMessages(List.of(msg));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AIRequest> entity = new HttpEntity<>(request,headers);
        ResponseEntity<AIResponse> response= restTemplate.postForEntity(url,entity,AIResponse.class);
        return response.getBody().getMessage().getContent();
    }

}

