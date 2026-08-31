package com.example.authservice.dto

import io.swagger.v3.oas.annotations.media.Schema


//this is the data user sends to register
data class RegisterRequest(
    @field:Schema(example = "Indu Bhushanam", description = "Full name of the user")  //we use this for swagger to give data in the request body
    val name: String,

    @field:Schema(example = "indu_dev", description = "Unique username")
    val username: String,

    @field:Schema(example = "indu@example.com", description = "Valid email address")
    val email: String,

    @field:Schema(example = "SecurePass123!", description = "Strong password")
    val password: String
)
