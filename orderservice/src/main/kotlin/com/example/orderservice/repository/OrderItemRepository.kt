package com.example.orderservice.repository

import com.example.orderservice.model.OrderItem
import org.springframework.data.jpa.repository.JpaRepository

interface OrderItemRepository: JpaRepository<OrderItem, Long> {
}