package ru.vladmz.books.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import java.time.LocalDateTime

import ru.vladmz.books.GlobalExceptionHandler
import ru.vladmz.books.services.PostService
import ru.vladmz.books.security.JwtUtil
import ru.vladmz.books.services.CustomUserDetailsService
import ru.vladmz.books.DTOs.post.PostRequest
import ru.vladmz.books.DTOs.post.PostPatchRequest
import ru.vladmz.books.DTOs.post.PostResponse
import ru.vladmz.books.exceptions.PostNotFoundException

@WebMvcTest(controllers = [PostController::class, GlobalExceptionHandler::class])
@AutoConfigureMockMvc(addFilters = false)
class PostControllerTest (@Autowired val mockMvc: MockMvc) {

    private val objectMapper = ObjectMapper().registerKotlinModule()

    @MockitoBean
    private lateinit var postService: PostService
    @MockitoBean
    lateinit var jwtUtil: JwtUtil
    @MockitoBean
    lateinit var customUserDetailsService: CustomUserDetailsService

    private lateinit var postResponse: PostResponse
    private val postId = 1
    private val postTitle = "Test Post"

    @BeforeEach
    fun setup() {
        postResponse = PostResponse(
            postId, null, postTitle, "Content", 0, 0, 0,
            LocalDateTime.now(), LocalDateTime.now()
        )
    }

    @Test
    fun findAll(){
        val posts = listOf(postResponse)
        whenever(postService.findAll()).thenReturn(posts)

        mockMvc.get("/posts") {
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.size()") { value(posts.size) }
        }
    }

    @Test
    fun findById(){
        whenever(postService.findById(postId)).thenReturn(postResponse)

        mockMvc.get("/posts/$postId") {
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(postId) }
        }
    }

    @Test
    fun findById_shouldReturn404() {
        val wrongId = 100
        whenever(postService.findById(wrongId)).thenThrow(PostNotFoundException(wrongId))

        mockMvc.get("/posts/$wrongId") {
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.message") { value("Post not found with id: $wrongId") }
        }
    }

    @Test
    fun createPost(){
        val request = PostRequest(postTitle, "Content")
        whenever(postService.savePost(any())).thenReturn(postResponse)

        mockMvc.post("/posts") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            jsonPath("$.title") { value(postTitle) }
        }
    }

    @Test
    fun createPost_shouldReturn400(){
        val request = PostRequest("", "Content")

        mockMvc.post("/posts") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun updatePost(){
        val request = PostPatchRequest().apply { title = "New Title" }
        val updatedResponse = PostResponse(postId, null, "New Title", "Content", 0,
            0, 0, LocalDateTime.now(), LocalDateTime.now())

        whenever(postService.updatePost(any(), eq(postId))).thenReturn(updatedResponse)

        mockMvc.patch("/posts/$postId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isOk() }
            jsonPath("$.title") { value("New Title") }
        }
    }

    @Test
    fun updatePost_shouldReturn404() {
        val wrongId = 100
        val request = PostPatchRequest().apply { title = "Updated Title" }

        whenever(postService.updatePost(any(), eq(wrongId)))
            .thenThrow(PostNotFoundException(wrongId))

        mockMvc.patch("/posts/$wrongId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.status") { value(404) }
            jsonPath("$.error") { value("Resource not found") }
            jsonPath("$.message") { value("Post not found with id: $wrongId") }
        }
    }

    @Test
    fun updatePost_shouldReturn403(){
        val request = PostPatchRequest().apply { title = "New Title" }
        whenever(postService.updatePost(any(), eq(postId)))
            .thenThrow(org.springframework.security.access.AccessDeniedException("Forbidden"))

        mockMvc.patch("/posts/$postId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isForbidden() }
            jsonPath("$.error") { value("Access denied") }
            jsonPath("$.message") { value("Forbidden") }
        }
    }

    @Test
    fun updatePost_shouldReturn405(){
        val request = PostPatchRequest().apply { title = "New Title" }

        mockMvc.post("/posts/$postId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
        }.andExpect {
            status { isMethodNotAllowed() }
            jsonPath("$.message") { value("Request method 'POST' is not supported") }
        }
    }

    @Test
    fun deletePost(){
        whenever(postService.deletePost(postId)).then{}

        mockMvc.delete("/posts/$postId"){}.andExpect {
            status { isNoContent() }
        }
        verify(postService).deletePost(postId)
    }

    @Test
    fun deletePost_shouldReturn404(){
        val wrongId = 100
        whenever(postService.deletePost(wrongId)).thenThrow(PostNotFoundException(wrongId))

        mockMvc.delete("/posts/$wrongId"){}.andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun deletePost_shouldReturn403() {
        whenever(postService.deletePost(postId))
            .thenThrow(org.springframework.security.access.AccessDeniedException("Forbidden"))

        mockMvc.delete("/posts/$postId") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isForbidden() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.status") { value(403) }
            jsonPath("$.error") { value("Access denied") }
            jsonPath("$.message") { value("Forbidden") }
        }
    }
}