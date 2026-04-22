package com.faizan.taskmanager.Service;

import com.faizan.taskmanager.Client.CacheService;
import com.faizan.taskmanager.Constant.ConfidenceLevel;
import com.faizan.taskmanager.Dto.EmbeddingRequest;
import com.faizan.taskmanager.Entity.ScoredEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KnowledgeQueryService {

    private final EmbeddingService embeddingService;
    private final RetrieverService retrieverService;
    private final CacheService cacheService;
    private final ConfidenceEvaluator confidenceEvaluator;
    private final QueueService queueService;
    private final RedisTemplate<String, String> redisTemplate;

    public String answerQueryFast(String query) throws InterruptedException {

        // Normalize query (VERY IMPORTANT)
        String normalizedQuery = query.trim().toLowerCase();

        // Create lock key using normalized query
        String lockKey = "lock:" + normalizedQuery;

        // Acquire lock FIRST (before cache check)
        Boolean acquired = redisTemplate.opsForValue()
                .setIfAbsent(lockKey, "1", Duration.ofSeconds(15)); // increased timeout

        // CASE: LOCK NOT ACQUIRED → WAIT
        if (!Boolean.TRUE.equals(acquired)) {

            log.info("LOCK NOT ACQUIRED → waiting for result");

            //Improved wait strategy (longer wait)
            for (int i = 0; i < 20; i++) {
                Optional<String> retry = cacheService.get(normalizedQuery);

                if (retry.isPresent()) {
                    log.info("CACHE HIT AFTER WAIT");
                    return retry.get();
                }

                Thread.sleep(100);
            }

            return "BUSY_TRY_AGAIN_LATER";
        }

        // CASE: LOCK ACQUIRED
        try {

            // Re-check cache INSIDE lock (double-check)
            Optional<String> cached = cacheService.get(normalizedQuery);
            if (cached.isPresent()) {
                log.info("CACHE HIT INSIDE LOCK");
                return cached.get();
            }

            Optional<String> pending = cacheService.get("pending:" + normalizedQuery);
            if (pending.isPresent()) {
                log.info("PENDING CACHE HIT INSIDE LOCK");
                return pending.get(); // return fast response
            }

            log.info("CACHE MISS → processing");

            // EMBEDDING
            List<Double> queryEmbedding =
                    embeddingService.embedText(new EmbeddingRequest(normalizedQuery));

            // RETRIEVAL
            List<ScoredEntry> results =
                    retrieverService.retrieve(queryEmbedding, 3, 0.60);

            //Handle empty results + enqueue (dedup not yet)
            if (results.isEmpty()) {
                // commented
//                enqueueIfNeeded(normalizedQuery, List.of());
                log.warn("No context → marking as insufficient: {}", query);

                cacheService.put(normalizedQuery, "INSUFFICIENT_CONTEXT");
                return "INSUFFICIENT_CONTEXT";
            }

            // CONFIDENCE
            ConfidenceLevel level = confidenceEvaluator.evaluate(results);
            log.info("Confidence Level: {}", level);

            ScoredEntry top = results.getFirst();

            Object desc = top.getEntry().getMetadata().getDescription();
            String response = desc != null ? desc.toString() : "NO DATA";

            // Deduplicate enqueue using Redis key
            String queueKey = "queued:" + normalizedQuery;

//            Boolean shouldEnqueue = redisTemplate.opsForValue()
//                    .setIfAbsent(queueKey, "1", Duration.ofMinutes(5));

            // DECISION
            switch (level) {

                case HIGH -> {
                    cacheService.put(normalizedQuery, response);
                    return response;
                }

                case MEDIUM -> {
                    enqueueIfNeeded(normalizedQuery, results);
                    cacheService.put("pending:" + normalizedQuery, response);
//                    cacheService.put(normalizedQuery, response);
                    return response;
                }

                case LOW -> {
                    enqueueIfNeeded(normalizedQuery, results);

                    log.warn("LOW confidence → returning fallback result");

                    String finalResponse = "[LOW_CONFIDENCE] " + response;

                    cacheService.put("pending:" + normalizedQuery, response);
//                    cacheService.put(normalizedQuery, finalResponse);

                    return finalResponse;
                }

                default -> {
                    return "INSUFFICIENT_CONTEXT";
                }
            }

        } finally {
            //release lock
            redisTemplate.delete(lockKey);
        }
    }
private void enqueueIfNeeded(String query, List<ScoredEntry> results) {
    String queueKey = "queued:" + query;

    Boolean shouldEnqueue = redisTemplate.opsForValue()
            .setIfAbsent(queueKey, "1", Duration.ofMinutes(5));

    if (Boolean.TRUE.equals(shouldEnqueue)) {
        log.info("ENQUEUE → {}", query);
        queueService.enqueue(query, results);
    } else {
        log.info("SKIP ENQUEUE (DEDUP) → {}", query);
    }
}
}

