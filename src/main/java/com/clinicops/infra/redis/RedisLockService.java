package com.clinicops.infra.redis;

import java.time.Duration;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RedisLockService {

    private final StringRedisTemplate redisTemplate;

    public boolean acquireLock(String key, Duration ttl) {
        Boolean success = redisTemplate
            .opsForValue()
            .setIfAbsent(key, "1", ttl);
        return Boolean.TRUE.equals(success);
    }

    public void releaseLock(String key) {
        redisTemplate.delete(key);
    }
}
