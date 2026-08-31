package com.example.authservice.service

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class JwtServiceTest {

    private lateinit var jwtService: JwtService

    @BeforeEach
    fun setup() {
        jwtService = JwtService()
    }

    @Test
    fun `test generate token and extract username`() {
        val username = "testuser"
        val token = jwtService.generateToken(username)

        assertNotNull(token)
        assertEquals(username, jwtService.extractUsername(token))
    }

    @Test
    fun `test valid token returns true`() {
        val username = "testuser"
        val token = jwtService.generateToken(username)

        assertTrue(jwtService.isValidToken(token))
    }



}