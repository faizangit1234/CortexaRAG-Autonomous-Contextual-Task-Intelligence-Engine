package com.faizan.taskmanager.Service;

import com.faizan.taskmanager.Entity.ScoredEntry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisQueueService implements QueueService{

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void enqueue(String query, List<ScoredEntry> results) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("query", query);
            payload.put("results", results);

            String json = objectMapper.writeValueAsString(payload);

            redisTemplate.opsForList().leftPush("llm_queue", json);
        }catch (Exception e){
            log.error("Queue push failed", e);
        }
    }
}
