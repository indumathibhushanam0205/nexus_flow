package com.example.orderservice.dto

import java.io.Serializable


data class OrderItemDto(
    val id: Long = 0,
    val productId: Long,
    val quantity: Int,
    val priceAtPurchase: Double
) : Serializable
