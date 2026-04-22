package com.faizan.taskmanager.worker.queue;

import com.faizan.taskmanager.worker.model.LLMJob;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class RedisQueueConsumer implements QueueConsumer{

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public LLMJob consume() {
        try {
            String json = redisTemplate.opsForList().rightPop("llm_queue");

            if (json == null) {
                return null; // defensive (should not happen with blocking)
            }

            return objectMapper.readValue(json, LLMJob.class);

        } catch (Exception e) {
            throw new RuntimeException("Failed to consume job: " + e.getMessage(), e);
        }
    }
}
