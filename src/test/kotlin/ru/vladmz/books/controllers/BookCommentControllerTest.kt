package ru.vladmz.books.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.argThat
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import ru.vladmz.books.DTOs.CommentTarget
import ru.vladmz.books.DTOs.comment.CommentPatchRequest
import ru.vladmz.books.DTOs.comment.CommentRequest
import ru.vladmz.books.DTOs.comment.CommentResponse
import ru.vladmz.books.entities.Comment
import ru.vladmz.books.entities.User
import ru.vladmz.books.etc.TargetType
import ru.vladmz.books.exceptions.CommentNotFoundException
import ru.vladmz.books.mappers.CommentMapper
import ru.vladmz.books.security.JwtUtil
import ru.vladmz.books.services.CommentService
import ru.vladmz.books.services.CustomUserDetailsService

@WebMvcTest(controllers = [BookCommentController::class])
@AutoConfigureMockMvc(addFilters = false)
class BookCommentControllerTest(@Autowired val mockMvc: MockMvc) {

    private val objectMapper = ObjectMapper().registerKotlinModule()

    @MockitoBean
    lateinit var commentService: CommentService
    @MockitoBean
    lateinit var jwtUtil: JwtUtil
    @MockitoBean
    lateinit var customUserDetailsService: CustomUserDetailsService

    lateinit var comment: Comment
    lateinit var user: User
    val commentId = 1
    val bookId = 5
    val userId = 10

    @BeforeEach
    fun setUp() {
        comment = Comment()
        user = User()
        user.id = userId
        comment.id = commentId
        comment.targetId = bookId
        comment.targetType = TargetType.BOOK
        comment.setUser(user)
    }

    @Test
    fun findByTargetId(){
        val response = CommentMapper.toResponse(comment)
        whenever(commentService.getCommentsByTargetId(any(), any()))
            .thenReturn(PageImpl(listOf(response)))

        mockMvc.get("/books/$bookId/comments") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.content[0].id") { value(commentId) }
        }
        verify(commentService).getCommentsByTargetId(
            argThat { it.type() == TargetType.BOOK && it.id() == bookId },
            any()
        )
    }

    @Test
    fun findById(){
        val response = CommentMapper.toResponse(comment)
        whenever(commentService.findById(any(), any())).thenReturn(response)

        mockMvc.get("/books/$bookId/comments/$commentId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(commentId) }
        }
    }

    @Test
    fun findById_shouldReturn404(){
        val wrongId = 100
        whenever(commentService.findById(any(), any())).thenThrow(CommentNotFoundException(wrongId))

        mockMvc.get("/books/$bookId/comments/$wrongId") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.status") { value(404) }
            jsonPath("$.error") { value("Resource not found") }
            jsonPath("$.message") { value("Comment not found with id: $wrongId") }
        }
    }

    @Test
    fun findReplies(){
        val response = CommentMapper.toResponse(comment)
        whenever(commentService.findReplies(any(), any(), any()))
            .thenReturn(PageImpl(listOf(response)))

        mockMvc.get("/books/$bookId/comments/$commentId/replies") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.content[0].id") { value(commentId) }
        }
        verify(commentService).findReplies(
            argThat { it.type() == TargetType.BOOK && it.id() == bookId },
            eq(commentId),
            any()
        )
    }

    @Test
    fun findReplies_shouldReturn404(){
        val wrongId = 100
        whenever(commentService.findReplies(any(), any(), any())).thenThrow(CommentNotFoundException(wrongId))

        mockMvc.get("/books/$bookId/comments/$wrongId/replies") {
            contentType = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.status") { value(404) }
            jsonPath("$.error") { value("Resource not found") }
            jsonPath("$.message") { value("Comment not found with id: $wrongId") }
        }
    }

    @Test
    fun createComment(){
        val response = CommentResponse.fromComment(comment).withText("Text")
        val request = CommentRequest("Text", 0)

        whenever(commentService.createComment(any(), any(), any())).thenReturn(response)

        mockMvc.post("/books/$bookId/comments") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            jsonPath("$.id") { value(response.id) }
            jsonPath("$.text") { value(response.text) }
        }
    }

    @Test
    fun createComment_shouldReturn400(){
        val blankTitle = "   "
        val response = CommentResponse.fromComment(comment).withText(blankTitle)
        val request = CommentRequest(blankTitle, 0)

        mockMvc.post("/books/$bookId/comments") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") { value ("text : must not be blank; ")}
        }
    }

    @Test
    fun updateComment(){
        val response = CommentResponse.fromComment(comment).withText("Text")
        val request = CommentPatchRequest("Text")

        whenever(commentService.updateComment(any(), any(), any())).thenReturn(response)

        mockMvc.patch("/books/$bookId/comments/$commentId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.id") { value(response.id) }
            jsonPath("$.text") { value(response.text) }
        }
    }

    @Test
    fun updateComment_shouldReturn404(){
        val wrongId = 100
        val response = CommentResponse.fromComment(comment).withText("Text")
        val request = CommentPatchRequest("Text")

        whenever(commentService.updateComment(any(), any(), any()))
            .thenThrow(CommentNotFoundException(wrongId))

        mockMvc.patch("/books/$bookId/comments/$wrongId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.status") { value(404) }
            jsonPath("$.error") { value("Resource not found") }
            jsonPath("$.message") { value("Comment not found with id: $wrongId") }
        }
    }

    @Test
    fun updateComment_shouldReturn400(){
        val blankTitle = "   "
        val response = CommentResponse.fromComment(comment).withText(blankTitle)
        val request = CommentPatchRequest(blankTitle)

        mockMvc.patch("/books/$bookId/comments/$commentId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") { value ("text : must not be blank; ")}
        }
    }

    @Test
    fun updateComment_shouldReturn403(){
        val response = CommentResponse.fromComment(comment).withText("Text")
        val request = CommentPatchRequest("Text")

        whenever(commentService.updateComment(any(), any(), any()))
            .thenThrow(AccessDeniedException("Forbidden"))

        mockMvc.patch("/books/$bookId/comments/$commentId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isForbidden() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.status") { value(403) }
            jsonPath("$.error") { value("Access denied") }
            jsonPath("$.message") { value("Forbidden") }
        }
    }

    @Test
    fun deleteComment(){
        whenever(commentService.deleteComment(any(), any())).then{}

        mockMvc.delete("/books/$bookId/comments/$commentId") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNoContent() }
        }
        verify(commentService).deleteComment(commentId, CommentTarget.ofBook(bookId))
    }

    @Test
    fun deleteComment_shouldReturn404(){
        val wrongId = 100

        whenever(commentService.deleteComment(any(), any()))
            .thenThrow(CommentNotFoundException(wrongId))

        mockMvc.delete("/books/$bookId/comments/$wrongId") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            jsonPath("$.status") { value(404) }
            jsonPath("$.error") { value("Resource not found") }
            jsonPath("$.message") { value("Comment not found with id: $wrongId") }
        }
    }

    @Test
    fun deleteComment_shouldReturn403(){
        whenever(commentService.deleteComment(any(), any()))
            .thenThrow(AccessDeniedException("Forbidden"))

        mockMvc.delete("/books/$bookId/comments/$commentId") {
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isForbidden() }
            jsonPath("$.status") { value(403) }
            jsonPath("$.error") { value("Access denied") }
            jsonPath("$.message") { value("Forbidden") }
        }
    }
}