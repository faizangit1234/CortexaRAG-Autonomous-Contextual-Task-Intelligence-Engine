package com.faizan.taskmanager.Service;

import com.faizan.taskmanager.Client.OllamaClient;
import com.faizan.taskmanager.Dto.EmbeddingRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService {
    private final OllamaClient ollamaClient;
    public EmbeddingService(OllamaClient ollamaClient){
        this.ollamaClient= ollamaClient;
    }

    public List<Double> embedText(EmbeddingRequest text){
        if(text== null){
            throw new IllegalArgumentException("Text Cannot be empty");
        }
        List<Double> doubles = ollamaClient.generateEmbedding(text.getPrompt());
        return doubles;

    }
}
