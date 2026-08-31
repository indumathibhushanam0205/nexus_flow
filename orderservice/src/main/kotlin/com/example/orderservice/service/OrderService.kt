package com.example.orderservice.service



import com.example.orderservice.dto.OrderDto
import com.example.orderservice.dto.OrderRequest
import com.example.orderservice.enums.OrderStatus
import com.example.orderservice.exception.*
import com.example.orderservice.kafka.OrderEventPublisher
import com.example.orderservice.model.Order
import com.example.orderservice.model.OrderItem
import com.example.orderservice.model.toDto
import com.example.orderservice.repository.OrderRepository
import com.example.orderservice.repository.ProductRepository
import jakarta.transaction.Transactional
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.cache.annotation.Caching
import org.springframework.cache.annotation.EnableCaching
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.PathVariable
import org.redisson.api.RedissonClient
import java.util.concurrent.TimeUnit
import org.springframework.cache.CacheManager     //dont import java cache


@EnableCaching
@Service
class OrderService(private val orderRepository: OrderRepository,
                   private val productRepository: ProductRepository,
                   private val redissonClient: RedissonClient,
                   private val cacheManager: CacheManager,
                   private val publisher: OrderEventPublisher)

{


    //----------------to place an order-------------------------------------------

    @Transactional //it is do all or do nothing
    fun placeOrder(orderRequest: OrderRequest): Order {


        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication == null || !authentication.isAuthenticated || authentication.name == "anonymousUser") {
            throw UnauthorizedException("User is not authenticated. Please log in.")
        }

        // 1. Sort IDs to prevent Deadlocks
        val sortedItems = orderRequest.items.sortedBy { it.productId }  //we will sort by product id
        val locks = sortedItems.map { redissonClient.getLock("lock:product:${it.productId}") }
        val multiLock = redissonClient.getMultiLock(*locks.toTypedArray())


        // 2. Try to lock with a timeout
        // Wait 5 seconds to get the lock, hold it for max 10 seconds

        if(!multiLock.tryLock(5,10, TimeUnit.SECONDS)){
            throw SystemBusyException("System is busy, please try again in a few seconds.")
        }



        val username = authentication.name
        val orderItems = mutableListOf<OrderItem>()
        var totalAmount = 0.0

        // Pass 1: Validation
        try {


            for (item in sortedItems) {
                val product = productRepository.findById(item.productId).orElse(null)
                    ?: throw ProductNotFoundException("Product ID ${item.productId} not found.")

                if (product.stockQuantity < item.quantity) {
                    throw InsufficientStockException("Not enough stock for ${product.name}. Available: ${product.stockQuantity}")
                }
            }

            for (item in sortedItems) {

                val product = productRepository.findById(item.productId).get()


                product.stockQuantity -= item.quantity
                productRepository.save(product)

                val orderItem = OrderItem(
                    productId = product.id,
                    quantity = item.quantity,
                    priceAtPurchase = product.price
                )

                orderItems.add(orderItem)
                totalAmount += (product.price * item.quantity)
            }

            val order = Order(
                username = username,
                totalAmount = totalAmount,
                status = OrderStatus.PLACED,
                items = orderItems,

                )

            clearCaches(username)      //we dont want use cache evict on the systems with locks even if it is unsuccessful cache can be removed so we use a seperate function and call it below
            val savedOrder = orderRepository.save(order)   //we are saving it first bcz when the order is sending to kafka without saving all ids are zero to avoid that we will first save then send it
            publisher.publishOrderPlaced(savedOrder)  //for kafka

            return savedOrder
        }

        finally {
            // 5. Release locks manually when finished
            multiLock.unlock()
        }


    }

    private fun clearCaches(username: String) {
        // Clear products cache
        cacheManager.getCache("products")?.clear()
        // Clear user-specific orders cache
        cacheManager.getCache("orders_by_user")?.evict(username)
    }

    @Transactional
    @CacheEvict(value = ["orders"], key = "#id")  //we use this bcz we are changing data and the redis data should be removed
    fun updateStatus(orderId: Long, newStatus: OrderStatus): Order {
        val order = orderRepository.findById(orderId)
            .orElseThrow { OrderNotFoundException("Order with ID $orderId not found") }

        order.status = newStatus
        return orderRepository.save(order)
    }

    @Transactional
    @CacheEvict(value = ["orders"], key = "#id")
    fun shipOrder(orderId: Long): Order {
        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication == null || !authentication.isAuthenticated || authentication.name == "anonymousUser") {
            throw UnauthorizedException("User is not authenticated. Please log in.")
        }

        // Check if the user has the ADMIN role
        val isAdmin = authentication.authorities.any { it.authority == "ROLE_ADMIN" }

        if (!isAdmin) {
            throw AccessDeniedException("You do not have the required permissions to perform this action Only admins can ship orders")
        }

        val order = orderRepository.findById(orderId).orElseThrow{ OrderNotFoundException("Order with ID $orderId not found") }
        order.status = OrderStatus.SHIPPED
        return orderRepository.save(order)
    }

    /**
     * Note: Returning DTOs instead of Entities prevents 'LazyInitializationException'.
     * Entities are tied to a closed Hibernate session, causing serialization failure
     * in Redis. DTOs are plain objects, making them safe for caching.
     * We use 'JOIN FETCH' to ensure collections are pre-loaded into memory
     * before the transaction closes.
     */


    @Cacheable(value = ["orders"], key = "#id")
    fun getOrder(@PathVariable id: Long): OrderDto {

        val order = orderRepository.findOrderByIdwithItems(id)
        .orElseThrow { OrderNotFoundException("Order with ID $id not found") }

        return order.toDto()
    }

    @Cacheable(value = ["orders_by_user"],
        key = "T(org.springframework.security.core.context.SecurityContextHolder).getContext().authentication.name")  //this is not handled by kotlin so we have to mention the complete import statement and mention it it in t<> wrapper class this is not kotlin this is spel
    fun getMyOrders():List<OrderDto>{

        val authentication = SecurityContextHolder.getContext().authentication

        if (authentication == null || !authentication.isAuthenticated || authentication.name == "anonymousUser") {
            throw UnauthorizedException("User is not authenticated. Please log in.")
        }

        val username = authentication.name

        return orderRepository.findOrderByUsername(username).map { it.toDto() }

    }







}

