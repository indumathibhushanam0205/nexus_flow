package com.example.orderservice.service

import com.example.orderservice.dto.ProductRequest
import com.example.orderservice.dto.ProductResponse
import com.example.orderservice.exception.ProductNotFoundException
import com.example.orderservice.model.Product
import com.example.orderservice.repository.ProductRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.EnableCaching
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {

    //-------------------------------------function to get all the products ------------------------------
    @Cacheable(value = ["products"]) // Default key is used (fine for parameterless methods)
    fun findAll(): List<Product> {
        return productRepository.findAll()
    }

    //-------------------------------------function to get specific product details  ---------------------


     @Cacheable(value = ["products"] ,key = "#request.name")
    fun findByName(request: ProductRequest): ProductResponse {

        val product = productRepository.findByName(request.name)?:throw ProductNotFoundException(request.name+" is not found")

        val productResponse = ProductResponse(
            name = product.name,
            price = product.price,
            stockQuantity = product.stockQuantity,
        )
        return productResponse
    }


}