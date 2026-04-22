package com.faizan.taskmanager.worker.processor;

import com.faizan.taskmanager.Client.CacheService;
import com.faizan.taskmanager.Client.ResponseValidatorServiceImpl;
import com.faizan.taskmanager.Dto.EmbeddingRequest;
import com.faizan.taskmanager.Entity.MetaData;
import com.faizan.taskmanager.Entity.ScoredEntry;
import com.faizan.taskmanager.Entity.VectorEntry;
import com.faizan.taskmanager.Repository.VectorStore;
import com.faizan.taskmanager.Service.AiService;
import com.faizan.taskmanager.Service.EmbeddingService;
import com.faizan.taskmanager.worker.model.LLMJob;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class LLMJobProcessor implements JobProcessor {

    private final AiService aiService;
    private final ResponseValidatorServiceImpl validator;
    private final CacheService cacheService;
    private final EmbeddingService embeddingService;
    private final VectorStore vectorStore;

    @Override
    public void process(LLMJob job) {
        String query = job.getQuery();
        List<ScoredEntry> results = job.getResults();

        log.info("Processing job: {}", query);
        try {

            if(cacheService.get(query).isPresent()){
                log.info("Already learned - skipping : {}", query);
                return;
            }

            String context = buildContext(results);

            String llmResponse = aiService.generateWithContext(query, context);

            String validated = validator.validate(llmResponse, results);

            log.info("Validated output : {}", validated);

            if ("INSUFFICIENT_CONTEXT".equalsIgnoreCase(validated)) {
                log.warn("Skipping storage ( insufficient context ) : {}", query);
                return;
            }


            cacheService.put(query, validated);

            cacheService.delete("pending:" + query);

            List<Double> embedding = embeddingService.embedText(new EmbeddingRequest(query + " " + validated));

            MetaData metaData = new MetaData(query, validated);

            UUID uuid = UUID.randomUUID();

            VectorEntry entry= new VectorEntry((uuid.getMostSignificantBits()&Long.MAX_VALUE) , embedding, metaData);

            vectorStore.save(entry);

            log.info("Knowledge stored successfully for query: {}", query);

        } catch (Exception e) {
            log.error("Worker failed for query: {}", query, e);
        }


    }

    private String buildContext(List<ScoredEntry> results) {
        if (results == null || results.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (ScoredEntry e : results) {
            sb.append("Task: ")
                    .append(e.getEntry().getMetadata().getTitle())
                    .append("\nDescription: ")
                    .append(e.getEntry().getMetadata().getDescription())
                    .append("\n\n");
        }

        return sb.toString();
    }
}