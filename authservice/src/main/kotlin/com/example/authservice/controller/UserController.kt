package com.example.authservice.controller

import com.example.authservice.repository.UserRepository
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.security.Principal


@RestController
@RequestMapping("/api/user")
class UserController(private val userRepository: UserRepository) {

    @GetMapping("/me")
    fun me(principal: Principal): ResponseEntity<Any> {
        val userName = principal.name

        val user = userRepository.findByUsername(userName)?:return  ResponseEntity.notFound().build()

        return ResponseEntity.ok(user)
    }

}