package com.example.authservice.repository

import com.example.authservice.model.RefreshToken
import com.example.authservice.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository


@Repository
interface RefreshTokenRepository : JpaRepository<RefreshToken, Long>{
    fun findByToken(token: String): RefreshToken?
    fun deleteByUser(user: User)
}