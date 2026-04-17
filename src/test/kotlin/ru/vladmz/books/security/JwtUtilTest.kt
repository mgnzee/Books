package ru.vladmz.books.security

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.test.util.ReflectionTestUtils
import ru.vladmz.books.entities.User
import ru.vladmz.books.etc.UserRole

class JwtUtilTest {

    private val jwtUtil = JwtUtil()

    val secret = "debug_secret_key_only_for_unit_tests_12345"
    val testUser = User(1, "testUser", "test@mail.com", "")

    @BeforeEach
    fun setup() {
        ReflectionTestUtils.setField(jwtUtil, "secret", secret)
        ReflectionTestUtils.setField(jwtUtil, "expiration", 3600000L)
    }

    @Test
    fun generateAndExtractToken() {
        val token = jwtUtil.generateToken(testUser)

        assertNotNull(token)
        assertEquals("test@mail.com", jwtUtil.extractUsername(token))
        assertEquals(1, jwtUtil.extractUserId(token))
        assertEquals(UserRole.USER.name, jwtUtil.extractUserRole(token))
    }

    @Test
    fun validateExpiredToken() {
        ReflectionTestUtils.setField(jwtUtil, "expiration", 0L)
        val token = jwtUtil.generateToken(testUser)

        assertFalse(jwtUtil.validateToken(token))
    }

    @Test
    fun validateInvalidToken() {
        val token = jwtUtil.generateToken(testUser)
        val invalidToken = token + "a"

        assertFalse(jwtUtil.validateToken(invalidToken))
    }

    @Test
    fun validateUserExtraction(){
        var token = jwtUtil.generateToken(testUser)

        assertEquals(UserRole.USER.name, jwtUtil.extractUserRole(token))

        testUser.role = UserRole.ADMIN
        token = jwtUtil.generateToken(testUser)

        assertEquals(UserRole.ADMIN.name, jwtUtil.extractUserRole(token))
    }

}