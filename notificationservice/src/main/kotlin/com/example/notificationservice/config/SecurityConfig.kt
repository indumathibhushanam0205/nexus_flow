package com.example.notificationservice.config

import com.example.notificationservice.security.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableMethodSecurity //for preauthorize function in controller to work we need this
class SecurityConfig(private val jwtAuthFilter: JwtAuthenticationFilter) {

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }  // Don't use sessions
            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Allows all requests without login for mwntioned there
                auth.anyRequest().authenticated()  //Require a token for everything else
            }
            .addFilterBefore(jwtAuthFilter , UsernamePasswordAuthenticationFilter::class.java)


        return http.build()
    }

}
