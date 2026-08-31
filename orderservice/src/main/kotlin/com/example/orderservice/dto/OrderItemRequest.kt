package com.example.orderservice.dto

data class OrderItemRequest(
    val productId: Long,
    val quantity: Int
) {
}
