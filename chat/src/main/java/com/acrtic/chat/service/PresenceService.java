package com.acrtic.chat.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class PresenceService {

    private final StringRedisTemplate redis;

    public PresenceService(StringRedisTemplate redis) {
        this.redis = redis;
    }

    public void userOnline(String username) {
        redis.opsForValue().set("online:" + username, "1", Duration.ofMinutes(5));
    }

    public void userOffline(String username) {
        redis.delete("online:" + username);
    }

    public boolean isOnline(String username) {
        return redis.hasKey("online:" + username);
    }
}
