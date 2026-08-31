package com.example.notificationservice.security



import com.example.notificationservice.service.JwtService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.JwtException
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter  //logic runs exactly only once for a http request

@Component
class JwtAuthenticationFilter(private val jwtService: JwtService) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {


        // 1. Get the Authorization header from the request
        val authHeader = request.getHeader("Authorization")

        // 2. Check if the header exists and starts with "Bearer "
        // If there is no token, just let the request proceed.
        // If the path is public, Spring Security will allow it.
        // If the path is protected, Spring Security will later reject it (403 Forbidden).
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response) // No token? Just continue to the next step
            return
        }

        // 3. Extract the token (remove "Bearer " prefix)
        val token = authHeader.substring(7)

        try {

            jwtService.validateToken(token)  //1. Validate (Throws specific exceptions if failed)
            val username = jwtService.extractUsername(token)
            val role = jwtService.extractRole(token)

            if (username != null && SecurityContextHolder.getContext().authentication == null) {
                val authorities = listOf(SimpleGrantedAuthority("ROLE_$role"))  //we added this bcoz now we have role in our code and this SimpleGrantedAuthority check for prefix ROLE and we have roles as user and admin
                val auth = UsernamePasswordAuthenticationToken(username, null, authorities) //add the above listed authorities in auth along with username
                SecurityContextHolder.getContext().authentication = auth
            }
            filterChain.doFilter(request, response)  //Pass the request to the next filter/controller


        }
        catch (e: ExpiredJwtException) {
            sendError(response, "Token has expired", HttpStatus.UNAUTHORIZED)
        }
        catch (e: JwtException) {
            sendError(response, "Invalid token", HttpStatus.UNAUTHORIZED)
        }





    }

    private fun sendError(response: HttpServletResponse, message: String, status: HttpStatus) {
        response.status = status.value()
        response.contentType = "application/json"
        response.writer.write("{\"error\": \"$message\"}")
    }

}