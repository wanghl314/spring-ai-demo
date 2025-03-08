package com.whl.spring.ai.demo.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPooled;

@Configuration
public class RedisVectorStoreConfig {
    private final RedisProperties redisProperties;

    public RedisVectorStoreConfig(RedisProperties redisProperties) {
        this.redisProperties = redisProperties;
    }

    @Bean
    public JedisPooled jedisPooled() {
        return new JedisPooled(
                this.redisProperties.getHost(),
                this.redisProperties.getPort(),
                this.redisProperties.getUsername(),
                this.redisProperties.getPassword());
    }

}
