package com.faizan.taskmanager.Service;

import com.faizan.taskmanager.Client.CacheService;
import com.faizan.taskmanager.Client.RedisCacheService;
import com.faizan.taskmanager.Client.ResponseValidatorServiceImpl;
import com.faizan.taskmanager.Dto.EmbeddingRequest;
import com.faizan.taskmanager.Entity.ScoredEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TaskQueryService {
    private final EmbeddingService embeddingService;
    private final RetrieverService retrieverService;
    private final ContextBuilderService contextBuilderService;
    private final AiService aiService;
    private final CacheService cacheService;
    private final ResponseValidatorServiceImpl responseValidatorService;


    public TaskQueryService(EmbeddingService embeddingService , RetrieverService retrieverService,
                            ContextBuilderService contextBuilderService,
                            AiService aiService,
                            CacheService cacheService,
                            ResponseValidatorServiceImpl responseValidatorService){
        this.aiService=aiService;
        this.contextBuilderService=contextBuilderService;
        this.embeddingService= embeddingService;
        this.retrieverService= retrieverService;
        this.cacheService = cacheService;
        this.responseValidatorService = responseValidatorService;

    }

    public String answerQuery(String query){

        Optional<String> cached = cacheService.get(query);

        if (cached.isPresent()) {
            log.info("CACHE HIT");
            return cached.get();
        } else {
            log.info("CACHE MISS");
        }

        EmbeddingRequest embeddingRequest= new EmbeddingRequest(query);

        //log query
        System.out.println("USER QUERY: " + query);

        List<Double> queryEmbedding= embeddingService.embedText(embeddingRequest);

        List<ScoredEntry> results =
                retrieverService.retrieve(queryEmbedding,3,0.60);

        if(results.isEmpty()){
            return "NO relevant information found";
        }

        //log results
        System.out.println("RETRIEVED RESULTS:");
        for (ScoredEntry e : results) {
            System.out.println(e.getEntry().getMetadata() + " | score=" + e.getScore());
        }

        String context = contextBuilderService.buildContext(results);

        // print context
        System.out.println("CONTEXT:\n" + context);

        String prompt = aiService.generateWithContext(query,context);

        //log prompt
        System.out.println("Initial PROMPT:\n" + prompt);

        String validated = responseValidatorService.validate(prompt,results);

        //log prompt
        System.out.println("validated PROMPT:\n" + validated);

        cacheService.put(query,validated);

        return validated;


    }
}
