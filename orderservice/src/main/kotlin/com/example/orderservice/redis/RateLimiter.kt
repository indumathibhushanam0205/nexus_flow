package com.example.orderservice.redis

import org.redisson.api.RRateLimiter
import org.redisson.api.RateIntervalUnit
import org.redisson.api.RateType
import org.redisson.api.RedissonClient
import org.springframework.stereotype.Service


@Service
class RateLimiter(private val redissonClient: RedissonClient) {

    fun isAllowed(username : String): Boolean {

        // Create a unique limiter for this user
        val limiter: RRateLimiter = redissonClient.getRateLimiter("rate_limit:$username")

        // Try to set the rate: 10 requests per 60 seconds (if not already set)
        limiter.trySetRate(RateType.OVERALL,10,60, RateIntervalUnit.SECONDS)

        // Attempt to acquire 1 permit
        return limiter.tryAcquire()

    }
}