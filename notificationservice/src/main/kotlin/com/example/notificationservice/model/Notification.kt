package com.example.notificationservice.model


import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDateTime


@Entity
@Table(name = "notifications")
@JsonIgnoreProperties(ignoreUnknown = true)
data class Notification(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long=0,
    val username: String,
    val orderId: Long,
    val message: String,
    val type : String, // e.g., "ORDER_PLACED"
    val createdAt: LocalDateTime = LocalDateTime.now()

) : Serializable //<--- This allows Redis to convert the object to bytes
