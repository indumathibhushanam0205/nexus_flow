package com.example.authservice.controller

import com.example.authservice.dto.*
import com.example.authservice.repository.UserRepository
import com.example.authservice.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/auth")  // All endpoints in this class will start with /auth
@Tag(name = "Authentication", description = "Endpoints for user registration and login") // for swagger
class AuthController(private val userRepository: UserRepository , private val authService: AuthService) {

    @PostMapping("/register")
    @Operation(summary = "Register a new user") //for swagger
    fun register(@RequestBody request: RegisterRequest): ResponseEntity<String> {
        // 1. Pass the request data to the Service layer
        // The Controller doesn't care HOW the user is saved, only that the Service does it
        val message = authService.registerUser(request)

        // 2. Return the result wrapped in a ResponseEntity
        // HttpStatus.CREATED (201) is the standard code for successful creation
        return ResponseEntity.status(HttpStatus.CREATED).body(message)
    }

    @PostMapping("/login")
    @Operation(summary = "Login to the system")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "Login successful"),
        ApiResponse(responseCode = "401", description = "Invalid credentials"),
        ApiResponse(responseCode = "404", description = "User not found")
    ]) //for swgger
    fun login(@RequestBody request: LoginRequest): ResponseEntity<AuthResponse>{
        val authResponse  = authService.login(request)
        return ResponseEntity.ok(authResponse )
    }

    @PostMapping("/refresh")
    fun refresh(@RequestBody request: RefreshTokenRequest): ResponseEntity<AuthResponse> {
        return ResponseEntity.ok(authService.refreshAccessToken(request.refreshToken))
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout from the system")
    fun logout(@RequestBody request: RefreshTokenRequest): ResponseEntity<String> {
        return authService.logout(request.refreshToken)
    }


}