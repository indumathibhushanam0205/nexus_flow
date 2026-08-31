package com.example.orderservice.config

import com.example.orderservice.model.Product
import com.example.orderservice.repository.ProductRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataSeeder {

    @Bean
    fun initDataSeeder(productRepository: ProductRepository): CommandLineRunner {

        return CommandLineRunner {
            if (productRepository.count() == 0L) {
                val products = listOf(
                    Product(name = "Laptop", price = 1200.0, stockQuantity = 10),
                    Product(name = "Headphones", price = 150.0, stockQuantity = 50),
                    Product(name = "Mechanical Keyboard", price = 100.0, stockQuantity = 30)
                )
                productRepository.saveAll(products)
            }
            println("Database seeded with initial products.")
        }

    }
}