package com.example.orderservice.dto

import com.example.orderservice.model.Product
import java.io.Serializable

data class ProductResponse(
    val name: String,
    val price: Double,
    val stockQuantity: Int
) : Serializable // <--- This allows Redis to convert the object to bytes
