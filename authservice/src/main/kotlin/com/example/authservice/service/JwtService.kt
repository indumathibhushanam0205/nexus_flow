package com.example.authservice.service

import com.example.authservice.model.User
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey


@Service
class JwtService {

    // This is the "Secret Key" used to sign the token.
    // It must be kept secret. If someone gets this, they can forge tokens.
    private val key: SecretKey = Keys.hmacShaKeyFor("a-very-long-secret-key-that-is-at-least-32-chars-long-12345678".toByteArray())

    /**
    * Generates a new JWT token for a user.
    * @param username The identifier of the user (e.g., email or username)
    * @return A signed JWT string
    */
    fun generateToken(user: User): String {
        return Jwts.builder()
            .subject(user.username)  // The username is stored inside the token
            .claim("role", user.role) //added this later as we order service required roles for the payment and shipping
            .issuedAt(Date())   // Current time
            .expiration(Date(System.currentTimeMillis()+600000)) // Token expires in 10 min
            .signWith(key, Jwts.SIG.HS256) // Sign it with our secret key
            .compact()  // Turn it into a string
    }

    fun generateRefreshToken(username: String): String {
        return Jwts.builder()
            .subject(username)
            .issuedAt(Date())
            .expiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7)) // 7 days
            .signWith(key, Jwts.SIG.HS256)
            .compact()
    }



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