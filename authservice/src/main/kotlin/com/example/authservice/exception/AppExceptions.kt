package com.example.authservice.exception


class UserNotFoundException(message: String) : RuntimeException(message)
class InvalidCredentialsException(message: String) : RuntimeException(message)
class UserAlreadyExistsException(message: String) : RuntimeException(message)
class InvalidTokenException(message: String) : RuntimeException(message)