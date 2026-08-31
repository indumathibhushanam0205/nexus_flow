package com.example.notificationservice.service

import com.example.notificationservice.exception.UnauthorizedException
import com.example.notificationservice.model.Notification
import com.example.notificationservice.repository.NotificationRepository
import jakarta.transaction.Transactional
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service


@Service
class NotificationService(private val notificationRepository: NotificationRepository) {

    @Transactional
    fun createNotification(username: String, orderId: Long, message: String, type: String) {
        val notification = Notification(username = username, orderId = orderId, message = message, type = type)
        notificationRepository.save(notification)
    }

    fun getMyNotifications(): List<Notification> {

        val authentication = SecurityContextHolder.getContext().authentication

        //println("DEBUG: Auth is: ${authentication?.name}, IsAuthenticated: ${authentication?.isAuthenticated}")

        if (authentication == null || !authentication.isAuthenticated || authentication.name == "anonymousUser") {
            throw UnauthorizedException("User is not authenticated. Please log in.")
        }

        val username = authentication.name

        return notificationRepository.findByUsernameOrderByCreatedAtDesc(username)


    }


    fun existsByOrderId(orderId: Long): Boolean {
        //This returns true if the orderId exists, false otherwise
         return notificationRepository.existsByOrderId(orderId)
    }



}