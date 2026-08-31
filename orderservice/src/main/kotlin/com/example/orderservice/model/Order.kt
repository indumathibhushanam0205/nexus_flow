package com.example.orderservice.model


import com.example.orderservice.dto.OrderDto
import com.example.orderservice.enums.OrderStatus
import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime


@Entity
@Table(name = "orders")
data class Order(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val username: String,
    var totalAmount: Double,

    @Enumerated(EnumType.STRING) // This is CRITICAL. It stores "PLACED" instead of the index (0, 1, 2)
    var status: OrderStatus = OrderStatus.PLACED,

    @OneToMany(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    val items: List<OrderItem> = mutableListOf(),

    val createdAt: LocalDateTime = LocalDateTime.now()
) : Serializable //<--- This allows Redis to convert the object to bytes

fun Order.toDto() = OrderDto(
    id = this.id,
    username = this.username,
    totalAmount = this.totalAmount,
    status = this.status,
    items = this.items.map { it.toDto() }, // Clean mapping
    createdAt = this.createdAt
)
