package com.example.orderservice.dto

import io.swagger.v3.oas.annotations.media.Schema


data class ProductRequest(
    @field:Schema(example = "Laptop", nullable = false, required = true)
    val name: String,
)
