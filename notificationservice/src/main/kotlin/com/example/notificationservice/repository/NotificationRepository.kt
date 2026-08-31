package com.example.notificationservice.repository

import com.example.notificationservice.model.Notification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.repository.query.Param

interface NotificationRepository : JpaRepository<Notification, Long> {


    fun existsByOrderId(orderId: Long): Boolean
    fun findByUsernameOrderByCreatedAtDesc(@Param("username") username: String): List<Notification>

}