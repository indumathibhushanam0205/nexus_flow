package com.example.orderservice.exception

class ProductNotFoundException(message: String) : RuntimeException(message)
class InsufficientStockException(message: String) : RuntimeException(message)
class UnauthorizedException(message: String) : RuntimeException(message)
class OrderNotFoundException(message: String) : RuntimeException(message)
class SystemBusyException(message: String) : RuntimeException(message)
class TooManyRequestsException(message: String) : RuntimeException(message)

