package com.example.orderservice.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.security.access.AccessDeniedException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler



@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(ProductNotFoundException::class)
    fun productNotFound(exception : ProductNotFoundException) : ResponseEntity<String> {
        logger.error("Product not found: {}", exception.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.message)
    }

    @ExceptionHandler(InsufficientStockException::class)
    fun insufficientStock(exception : InsufficientStockException) : ResponseEntity<String> {
        logger.warn("Insufficient stock error: {}", exception.message)
        return ResponseEntity.status(HttpStatus.CONFLICT).body(exception.message)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun unauthorized(exception : UnauthorizedException) : ResponseEntity<String> {
        logger.warn("Unauthorized access attempt: {}", exception.message)
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.message)
    }

    @ExceptionHandler(OrderNotFoundException::class)
    fun orderNotFound(exception : OrderNotFoundException) : ResponseEntity<String> {
        logger.warn("Order not found: {}", exception.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.message)
    }

    @ExceptionHandler(SystemBusyException::class)
    fun systemBusy(exception : SystemBusyException) : ResponseEntity<String> {
        logger.warn("System busy error: {}", exception.message)
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(exception.message)
    }

    @ExceptionHandler(TooManyRequestsException::class)
    fun tooManyRequests(exception : TooManyRequestsException) : ResponseEntity<String> {
        logger.warn("Too many requests error: {}", exception.message)
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(exception.message)
    }

    //we are adding this because when spring get  broken json then it throws HttpMessageNotReadableException which we will read as 403 to avoid that we are adding this error
    @ExceptionHandler(HttpMessageNotReadableException::class) //spring has it by default no need to define in custom exceptiom
    fun handleJsonError(ex: HttpMessageNotReadableException): ResponseEntity<String> {
        logger.error("Malformed JSON request: {}", ex.message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body("Invalid JSON format. Please check your commas and brackets.")
    }

    //pre authorize role throws below error not from code but spring security when admin role is not found in token
   @ExceptionHandler(AccessDeniedException::class)  //spring has it by default no need to define in custom exceptiom
    fun handleAccessDenied(ex : AccessDeniedException ) : ResponseEntity<String> {
        logger.warn("Security violation: {}","only admin can ship or pay orders") //
        return ResponseEntity
            .status(HttpStatus.FORBIDDEN)
            .body("You do not have the required permissions to perform this action! Only admins can ship or pay orders.")
    }
}