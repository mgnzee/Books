package ru.vladmz.books.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import ru.vladmz.books.DTOs.LoginRequest
import ru.vladmz.books.entities.User
import ru.vladmz.books.security.JwtUtil
import ru.vladmz.books.services.CustomUserDetailsService

@WebMvcTest(controllers = [AuthController::class])
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest (
    @Autowired val mockMvc: MockMvc,
){

    private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule()
    @MockitoBean
    lateinit var authManager: AuthenticationManager
    @MockitoBean
    lateinit var jwtUtil: JwtUtil
    @MockitoBean
    lateinit var customUserDetailsService: CustomUserDetailsService

    @Test
    fun login() {
        val user = User()
        user.email = "test@example.com"
        val loginRequest = LoginRequest("text@example.com", "password")

        val auth = UsernamePasswordAuthenticationToken(user, null, emptyList())

        whenever(authManager.authenticate(any())).thenReturn(auth)
        whenever(jwtUtil.generateToken(user)).thenReturn("token")

        mockMvc.post("/auth/login") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(loginRequest)
        }.andExpect {
            status { isOk() }
            jsonPath("$.token") { value("token") }
        }
    }
}