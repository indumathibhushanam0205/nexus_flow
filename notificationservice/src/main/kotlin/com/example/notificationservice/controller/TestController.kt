package com.example.notificationservice.controller

import org.springframework.web.bind.annotation.*

@RestController
class TestController {

    @GetMapping("/test")
    fun test(): String {
        return "notification service is connected to the database!"
    }
}