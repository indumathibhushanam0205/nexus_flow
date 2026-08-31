package com.example.orderservice.repository

import com.example.orderservice.model.Order
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.util.Optional

interface OrderRepository: JpaRepository<Order, Long> {
    fun findOrderById(id: Long): Optional<Order>

    // Explicitly link the :id in the query to the orderId parameter
    //this for redis as we face issue with fetch lazy type we are fetc
    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    fun findOrderByIdwithItems(@Param("id") id: Long): Optional<Order>

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.username = :username")
    fun findOrderByUsername(@Param("username") username: String): List<Order>
}