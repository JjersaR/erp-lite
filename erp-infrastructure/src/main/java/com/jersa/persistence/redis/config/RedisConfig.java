package com.jersa.persistence.redis.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.jersa.constants.CacheConstants.*;

@Configuration
@EnableCaching
public class RedisConfig {
    private static final Duration REDIS_CACHE_TTL = Duration.ofHours(24);

    @Bean // Enable annotations
    public RedisCacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
        GenericJacksonJsonRedisSerializer serializer = GenericJacksonJsonRedisSerializer.builder()
                .build();

        RedisCacheConfiguration config =
                RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(REDIS_CACHE_TTL)
                        .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                        .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer));

        Map<String, RedisCacheConfiguration> configsMap = new HashMap<>();
        configsMap.put(CACHE_PRODUCTS_BY_ID, config);
        configsMap.put(CACHE_PRODUCTS_BY_SKU, config);
        configsMap.put(CACHE_PRODUCTS_BY_CATEGORY, config);
        configsMap.put(CACHE_PRODUCTS_ACTIVE, config);
        configsMap.put(CACHE_CATALOGS_BY_TYPE, config);
        configsMap.put(CACHE_CATALOGS_ITEMS, config);

        return RedisCacheManager.builder(redisConnectionFactory).cacheDefaults(config)
                .withInitialCacheConfigurations(configsMap).build();
    }

    @Bean // Template Pattern
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        GenericJacksonJsonRedisSerializer serializer = GenericJacksonJsonRedisSerializer.builder()
                .build();

        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();

        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // CacheManager - String
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(serializer);

        // RedisTemplate - HashMap
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(serializer);

        return redisTemplate;
    }
}