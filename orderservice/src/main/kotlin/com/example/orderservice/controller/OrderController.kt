package com.example.orderservice.controller

import com.example.orderservice.dto.OrderDto
import com.example.orderservice.dto.OrderRequest
import com.example.orderservice.dto.ProductRequest
import com.example.orderservice.dto.ProductResponse
import com.example.orderservice.enums.OrderStatus
import com.example.orderservice.exception.TooManyRequestsException
import com.example.orderservice.exception.UnauthorizedException
import com.example.orderservice.model.Order
import com.example.orderservice.model.Product
import com.example.orderservice.redis.RateLimiter
import com.example.orderservice.repository.ProductRepository
import com.example.orderservice.service.OrderService
import com.example.orderservice.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api")

class OrderController(private val orderService: OrderService , private val ratesLimiter: RateLimiter) {

    private val logger = LoggerFactory.getLogger(OrderController::class.java)


    @PostMapping("/place-order")
    @Operation(summary = "place Order")
    fun create_order(@RequestBody request: OrderRequest):ResponseEntity<Order>{

        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication == null || !authentication.isAuthenticated || authentication.name == "anonymousUser") {
            throw UnauthorizedException("User is not authenticated. Please log in.")
        }

        val username = authentication.name

        if(!ratesLimiter.isAllowed(username)) {
            throw TooManyRequestsException("Rate limit exceeded")
        }


        val order = orderService.placeOrder(request)
        return ResponseEntity.ok(order)

    }

    /*
     *Updates the order status to PAID.
     * @param id The unique identifier of the order.
     *@throws AccessDeniedException if the current user lacks the ADMIN role.
      * Handled globally by [GlobalExceptionHandler] to return a specific business message.
     */

    @GetMapping("/{id}")
    fun getOrder(@PathVariable id: Long): ResponseEntity<OrderDto> {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication == null || !authentication.isAuthenticated || authentication.name == "anonymousUser") {
            throw UnauthorizedException("User is not authenticated. Please log in.")
        }

        val username = authentication.name

        if(!ratesLimiter.isAllowed(username)) {
            throw TooManyRequestsException("Rate limit exceeded")
        }

        return ResponseEntity.ok(orderService.getOrder(id))
    }

    /**
     * Note: Returning DTOs instead of Entities prevents 'LazyInitializationException'.
     * Entities are tied to a closed Hibernate session, causing serialization failure
     * in Redis. DTOs are plain objects, making them safe for caching.
     * We use 'JOIN FETCH' to ensure collections are pre-loaded into memory
     * before the transaction closes.
     */


    @GetMapping("/my")
    fun getMyOrders(): ResponseEntity<List<OrderDto>> {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication == null || !authentication.isAuthenticated || authentication.name == "anonymousUser") {
            throw UnauthorizedException("User is not authenticated. Please log in.")
        }

        val username = authentication.name

        if(!ratesLimiter.isAllowed(username)) {
            throw TooManyRequestsException("Rate limit exceeded")
        }

        // You'll need to extract the username from SecurityContext
        return ResponseEntity.ok(orderService.getMyOrders())
    }

    @PatchMapping("/{id}/pay")
    @PreAuthorize("hasRole('ADMIN')")   //checks if we have a role admin if present it works if not does not workm
    fun payOrder(@PathVariable id: Long): ResponseEntity<Order> {
        logger.info("Pay Order with id: $id")
        return ResponseEntity.ok(orderService.updateStatus(id, OrderStatus.PAID))
    }

    @PatchMapping("/{id}/ship")
    fun shipOrder(@PathVariable id: Long): ResponseEntity<Order> {
        return ResponseEntity.ok(orderService.shipOrder(id))
    }





}