package com.faizan.taskmanager.Client;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RedisCacheService implements CacheService{

    private final StringRedisTemplate stringRedisTemplate;

    @Override
    public Optional<String> get(String key) {
        String value = stringRedisTemplate.opsForValue().get(normalize(key));
        return Optional.ofNullable(value);
    }

    private String normalize(String input) {
        return input.toLowerCase().trim();
    }

    @Override
    public void put(String key, String value) {
        stringRedisTemplate.opsForValue().set(
                normalize(key),
                value,
                Duration.ofMinutes(30)
        );

    }

    @Override
    public void delete(String key) {
        stringRedisTemplate.delete(normalize(key));
    }
}
