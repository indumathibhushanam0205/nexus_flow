package com.example.authservice.controller

import org.springframework.web.bind.annotation.*

@RestController
class TestController {

    @GetMapping("/test")
    fun test(): String {
        return "Auth Service is connected to the database!"
    }
}