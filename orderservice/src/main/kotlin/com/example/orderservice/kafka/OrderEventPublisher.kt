package com.example.orderservice.kafka

import com.example.orderservice.model.Order
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service
import java.util.UUID


@Service
class OrderEventPublisher(private val kafkaTemplate: KafkaTemplate<String, Any>) {

    fun publishOrderPlaced(order: Order) {
        val event = mapOf(
            "eventId" to UUID.randomUUID().toString(),
            "eventName" to "ORDER_PLACED",
            "timestamp" to System.currentTimeMillis(),
            "payload" to order
        )

        kafkaTemplate.send("order.placed",order.id.toString(),event)

    }


}