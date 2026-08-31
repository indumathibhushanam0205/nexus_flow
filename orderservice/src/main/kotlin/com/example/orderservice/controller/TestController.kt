package com.example.orderservice.controller

import org.springframework.web.bind.annotation.*

@RestController
class TestController {

    @GetMapping("/test")
    fun test(): String {
        return "order service is connected to the database!"
    }
}