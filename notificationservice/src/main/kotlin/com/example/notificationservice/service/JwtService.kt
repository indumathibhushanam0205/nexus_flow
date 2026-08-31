package com.example.notificationservice.service


import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey


@Service
class JwtService {

    // This is the "Secret Key" used to sign the token.
    // It must be kept secret. If someone gets this, they can forge tokens.
    private val key: SecretKey = Keys.hmacShaKeyFor("a-very-long-secret-key-that-is-at-least-32-chars-long-12345678".toByteArray())


    //no need to generate tokens

    /**
     * Extracts the username (subject) from the token.
     * This method assumes the token is already validated.
     */

    fun extractUsername(token: String): String {

        return Jwts.parser()
            .verifyWith(key)  // Set the key to verify the signature
            .build().parseSignedClaims(token) // Parses the token and checks the signature
            .payload
            .subject  // Returns the username
    }

    fun extractRole(token: String): String {

        return Jwts.parser()
            .verifyWith(key)  // Set the key to verify the signature
            .build().parseSignedClaims(token) // Parses the token and checks the signature
            .payload
            .get("role", String::class.java)  // Returns the username
    }

    /**
     * Validates the token.
     * Returns true if the token is properly signed and not expired.
     */

    // This now throws JwtException or ExpiredJwtException automatically
    fun validateToken(token: String) {
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
    }


}