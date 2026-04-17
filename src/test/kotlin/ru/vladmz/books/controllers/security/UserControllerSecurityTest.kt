package ru.vladmz.books.controllers.security

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.*
import ru.vladmz.books.services.UserService
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerSecurityTest {

    @Autowired lateinit var mockMvc: MockMvc

    @MockitoBean lateinit var userService: UserService

    val userId = 1;

    @Test
    fun disableUser(){
        mockMvc.patch("/users/$userId/disable"){
            with(user("admin").roles("ADMIN"))
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun disableUser_shouldReturn403(){
        mockMvc.patch("/users/$userId/disable"){
            with(user("username").roles("USER"))
        }.andExpect {
            status { isForbidden() }
        }
    }

    @Test
    fun enableUser(){
        mockMvc.patch("/users/$userId/enable"){
            with(user("admin").roles("ADMIN"))
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun enableUser_shouldReturn403(){
        mockMvc.patch("/users/$userId/enable"){
            with(user("username").roles("USER"))
        }.andExpect {
            status { isForbidden() }
        }
    }

    @Test
    fun deleteUser(){
        mockMvc.delete("/users/$userId/permanent"){
            with(user("admin").roles("ADMIN"))
        }.andExpect {
            status { isNoContent() }
        }
    }

    @Test
    fun deleteUser_shouldReturn403(){
        mockMvc.delete("/users/$userId/permanent"){
            with(user("user").roles("USER"))
        }.andExpect {
            status { isForbidden() }
        }
    }
}