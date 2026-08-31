package com.example.orderservice.controller

import com.example.orderservice.dto.ProductRequest
import com.example.orderservice.dto.ProductResponse
import com.example.orderservice.model.Product
import com.example.orderservice.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/products")
class ProductController(private val productService: ProductService) {

    private val logger = LoggerFactory.getLogger(OrderController::class.java)

    @GetMapping("/get-stock")
    @Operation(summary = "total Stock ")
    fun getProducts(): List<Product> {
        return productService.findAll()
    }

    //to get details of specific product
    @GetMapping("/search")
    @Operation(summary = "Search Products")
    fun get_product_by_name(request : ProductRequest): ResponseEntity<ProductResponse>{
        val productResponse = productService.findByName(request)
        return ResponseEntity.ok(productResponse)

    }
}