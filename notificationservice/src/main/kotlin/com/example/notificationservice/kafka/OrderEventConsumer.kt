package com.example.notificationservice.kafka


import com.example.notificationservice.service.NotificationService
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.redisson.api.RedissonClient
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class OrderEventConsumer(
    private val notificationService: NotificationService,
    private val objectMapper: ObjectMapper ,// Inject Jackson's ObjectMapper
    private val redissonClient: RedissonClient
) {

    @KafkaListener(topics = ["order.placed"], groupId = "notification-group-v3")
    fun handleOrderPlaced(message: String) {
        val event = objectMapper.readValue(message, object : TypeReference<Map<String, Any>>() {})
        val payload = event["payload"] as Map<*, *>
        val orderId = (payload["id"] as Int).toLong()
        val username = payload["username"] as String // Extract username

        if (notificationService.existsByOrderId(orderId)) {
            println("Order $orderId already exists in DB. Skipping.")
            return
        }

        val lock = redissonClient.getLock("lock:order:$orderId")
        if (lock.tryLock(0, 5, TimeUnit.SECONDS)) {
            try {
                // Double-check inside the lock
                if (!notificationService.existsByOrderId(orderId)) {
                    notificationService.createNotification(
                        username = username,
                        orderId = orderId,
                        message = "Your order #$orderId has been placed successfully!",
                        type = "ORDER_PLACED"
                    )
                }
            } finally {
                lock.unlock()
            }
        } else {
            println("Could not acquire lock for order $orderId. Skipping.")
        }
    }



}
