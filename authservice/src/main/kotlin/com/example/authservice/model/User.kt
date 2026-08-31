package com.example.authservice.model

import jakarta.persistence.*

@Entity  // Marks this class as a database table for Hibernate
@Table(name = "users") // Maps this class to a specific table named 'users' in Postgres
// 'data class' automatically generates toString, equals, and hashCode
data class User(

    @Id  // Specifies that 'id' is the Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Tells Postgres to auto-increment the ID
    val id: Long = 0, // 'val' means immutable; '= 0' provides a default value for Hibernate

    @Column(nullable = false) //'name' cannot be empty in the database
    val name: String = "",

    @Column(unique = true,  nullable = false)
    val email: String = "",

    @Column(unique = true,  nullable = false) // added this later for unique login using username or email
    val username: String = "",

    @Column(nullable = false)
    var password: String = "", //'var' means this can be changed (e.g., when hashing it)

    @Column(nullable = false)
    val role: String = "USER" // added this as in order service payment and shippping should be done by user not by everyone
)
