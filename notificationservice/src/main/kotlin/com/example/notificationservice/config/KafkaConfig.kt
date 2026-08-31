package com.example.notificationservice.config

import org.apache.kafka.common.TopicPartition
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer
import org.springframework.kafka.listener.DefaultErrorHandler
import org.springframework.util.backoff.FixedBackOff

@Configuration
class KafkaConfig {

    @Bean

    fun errorHandle(kafkaTemplate: KafkaTemplate<String, String>): DefaultErrorHandler {

        // 1. Define how to handle the "dead" message
        val deadLetterPublishingRecoverer = DeadLetterPublishingRecoverer(kafkaTemplate) { record, ex ->

            // This creates a topic named: original-topic-name.DLQ
             TopicPartition("${record.topic()}.DLQ", record.partition())

        }

        // 2. Retry 3 times with a 1-second delay between each

        val errorHandler = DefaultErrorHandler(deadLetterPublishingRecoverer, FixedBackOff(1000L,3))

        return errorHandler
    }
}