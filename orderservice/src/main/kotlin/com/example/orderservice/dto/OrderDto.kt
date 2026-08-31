package com.example.orderservice.dto

import com.example.orderservice.enums.OrderStatus
import java.io.Serializable
import java.time.LocalDateTime

data class OrderDto(
    val id: Long = 0,
    val username: String,
    var totalAmount: Double,
    var status: OrderStatus = OrderStatus.PLACED,
    val items: List<OrderItemDto> = mutableListOf(),

    val createdAt: LocalDateTime = LocalDateTime.now()
) : Serializable
