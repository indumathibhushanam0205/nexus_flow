package com.example.orderservice.model

import com.example.orderservice.dto.OrderItemDto
import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "order_item")
data class OrderItem(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val productId: Long,
    val quantity: Int,
    val priceAtPurchase: Double
) : Serializable

//redis connection will be closed if we use model class so we used dto but to map thr data we should write very long functions

fun OrderItem.toDto() = OrderItemDto(
    id = this.id,
    productId = this.productId,
    quantity = this.quantity,
    priceAtPurchase = this.priceAtPurchase
)

