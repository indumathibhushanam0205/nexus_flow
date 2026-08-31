package com.example.orderservice.dto

data class OrderRequest(
    val items: List<OrderItemRequest>
)
