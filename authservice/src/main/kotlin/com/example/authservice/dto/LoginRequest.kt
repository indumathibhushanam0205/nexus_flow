package com.example.authservice.dto

import io.swagger.v3.oas.annotations.media.Schema

data class LoginRequest(
    @field:Schema(example = "indu@example.com")
    val identifier: String, // This will hold either the username or the email

    @field:Schema(example = "SecurePass123!", description = "Strong password")
    val password: String
)
