package com.example.orderservice.config

import org.redisson.api.RedissonClient
import org.redisson.spring.cache.CacheConfig
import org.redisson.spring.cache.RedissonSpringCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.cache.CacheManager


//we implemented this bcz redis is not taking ttl from application properties files so to ensure ttl we use this class
@Configuration
class RedisCacheConfig {

    @Bean
    fun cacheManager(redissonClient: RedissonClient): CacheManager {

        val config = mutableMapOf<String, CacheConfig>()

        val ttlConfig = CacheConfig()
        ttlConfig.ttl = 3600000 // 1 hour in milliseconds

        config["products"] = ttlConfig
        config["orders"] = ttlConfig
        config["orders_by_user"] = ttlConfig

        return RedissonSpringCacheManager(redissonClient, config)


    }
}