package com.example.authservice.service

import com.example.authservice.exception.InvalidCredentialsException
import com.example.authservice.exception.UserNotFoundException
import com.example.authservice.dto.*
import com.example.authservice.exception.UserAlreadyExistsException
import com.example.authservice.model.RefreshToken
import com.example.authservice.model.User
import com.example.authservice.repository.RefreshTokenRepository
import com.example.authservice.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.temporal.ChronoUnit

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtService: JwtService) {

    // Create a logger instance for this class
    private val logger = LoggerFactory.getLogger(AuthService::class.java)



    //-------------------------------------function to register the users in the db------------------------------
    fun registerUser(request: RegisterRequest): String {
        // Business Rule: Check if user already exists
        logger.info("Attempting to register user with email: ${request.email}")

        // 1. Check if the email is already taken by ANYONE
        if(userRepository.existsByEmail(request.email))
        {
            logger.warn("Registration failed Email : ${request.email} is already registered")
            throw UserAlreadyExistsException("Email is already registered!")
        }

        // 2. Check if the username is already taken by ANYONE
        if(userRepository.existsByUsername(request.username))
        {
            logger.warn("Registration failed username : ${request.username} is already registered")
            throw UserAlreadyExistsException("username is already registered!")
        }

        // 3. CROSS-CHECK:
        // Ensure the requested username isn't someone else's email
        if (userRepository.existsByEmail(request.username)) {
            throw UserAlreadyExistsException("Username cannot be someone else's email!")
        }

        // Ensure the requested email isn't someone else's username
        if (userRepository.existsByUsername(request.email)) {
            throw UserAlreadyExistsException("Email cannot be someone else's username!")
        }

        val hashedPassword = passwordEncoder.encode(request.password) ?: ""  //we are making sure the string is not null the spring thinks password encoder returms null but our password cannot be null

        val user = User(
            email = request.email,
            password = hashedPassword,
            name = request.name,
            username = request.username,
        )

        userRepository.save(user)
        logger.info("User registered successfully: UserName= ${request.username} , email = ${request.name}")

        return user.name + " has been successfully registered!"

    }


    //-------------------------------------function to login ------------------------------
    @Transactional
    fun login(request: LoginRequest): AuthResponse {

        // 1. Find the user
        val user = userRepository.findByEmail(request.identifier)
            ?: userRepository.findByUsername(request.identifier)
            ?: throw UserNotFoundException("User not found!") // Throws custom exception

        // 2. Check password
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw InvalidCredentialsException("Invalid credentials!") // Throws custom exception
        }

        refreshTokenRepository.deleteByUser(user)  // every user should have only one token at a time
        refreshTokenRepository.flush() //some times spring dont run all the sql queries immediately waits for the end of transcation but to create new one we must delete old as as we cant have one user with 2 refresh tokens



        val accessToken = jwtService.generateToken(user)
        val refreshToken = jwtService.generateRefreshToken(user.username)

        val refreshTokenEntity = RefreshToken(
            token = refreshToken,
            expiryDate = Instant.now().plus(7, ChronoUnit.DAYS),
            user = user
        )

        refreshTokenRepository.save(refreshTokenEntity)

        return AuthResponse(accessToken, refreshToken)
    }

    //-------------------------------------function to use refrsh token ------------------------------
    fun refreshAccessToken(refreshToken: String): AuthResponse{
        val tokenEntity = refreshTokenRepository.findByToken(refreshToken)?:throw Exception("Invalid refresh token")

        //Check if expired
        if(tokenEntity.expiryDate == null || tokenEntity.expiryDate.isBefore(Instant.now())){
            refreshTokenRepository.delete(tokenEntity)
            throw Exception("Refresh token expired")
        }

        // 3. Generate a new Access Token
        val newAccessToken = jwtService.generateToken(tokenEntity.user)

        return AuthResponse(
            accessToken = newAccessToken,
            refreshToken = refreshToken // Returning the same one, or generate a new one ("Rotation")
        )

    }

    //-------------------------------------function to logout user ------------------------------

    @Transactional
    fun logout(refreshToken: String): ResponseEntity<String> {
        val tokenEntity = refreshTokenRepository.findByToken(refreshToken) ?: throw Exception("Invalid refresh token")

        val username = tokenEntity.user.username
        refreshTokenRepository.delete(tokenEntity)
        return ResponseEntity.ok("$username has been successfully logged out!")
    }



}