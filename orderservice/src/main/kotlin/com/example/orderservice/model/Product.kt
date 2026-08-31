package com.example.orderservice.model

import jakarta.persistence.*
import java.io.Serializable

@Entity
@Table(name = "products")
data class Product(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(unique = true,  nullable = false)
    val name: String,

    @Column(nullable = false)
    val price: Double,

    @Column(nullable = false)
    var stockQuantity: Int
) : Serializable // <--- This allows Redis to convert the object to bytes
