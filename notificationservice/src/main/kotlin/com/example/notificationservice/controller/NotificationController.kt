package com.example.notificationservice.controller

import com.example.notificationservice.model.Notification
import com.example.notificationservice.service.NotificationService
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("/notifications")
class NotificationController(private val notificationService: NotificationService) {

    @GetMapping("/my_notifications")
    fun getmyNotifications(): ResponseEntity<List<Notification>> {
        return ResponseEntity.ok(notificationService.getMyNotifications())
    }
}