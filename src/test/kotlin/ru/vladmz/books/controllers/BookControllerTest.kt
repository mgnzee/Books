package ru.vladmz.books.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import ru.vladmz.books.entities.Book
import ru.vladmz.books.exceptions.BookNotFoundException
import ru.vladmz.books.mappers.BookMapper
import ru.vladmz.books.security.JwtUtil
import ru.vladmz.books.services.BookService
import ru.vladmz.books.services.CustomUserDetailsService

@WebMvcTest(controllers = [BookController::class])
@AutoConfigureMockMvc(addFilters = false)
class BookControllerTest (@Autowired val mockMvc: MockMvc) {

    private val objectMapper = ObjectMapper().registerKotlinModule()

    @MockitoBean
    private lateinit var bookService: BookService
    @MockitoBean
    lateinit var jwtUtil: JwtUtil
    @MockitoBean
    lateinit var customUserDetailsService: CustomUserDetailsService

    private lateinit var book: Book
    private val bookId = 1
    private val bookTitle = "White Nights"

    @BeforeEach
    fun setup() {
        book = Book()
        book.id = bookId
        book.title = bookTitle
    }

    @Test
    fun findById(){
        val bookResponse = BookMapper.toResponse(book)
        whenever(bookService.findById(bookId)).thenReturn(bookResponse)

        mockMvc.get("/books/$bookId") {
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(bookId) }
            jsonPath("$.title") { value(bookTitle) }
        }
    }

    @Test
    fun findById_shouldReturn404() {
        val wrongId = 100
        whenever(bookService.findById(wrongId)).thenThrow(BookNotFoundException(wrongId))

        mockMvc.get("/books/$wrongId") {
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.status") { value(404) }
            jsonPath("$.error") { value("Resource not found") }
            jsonPath("$.message") { value("Book not found with id: $wrongId") }
        }
    }

    @Test
    fun findAll(){

    }

    @Test
    fun createBook(){

    }
}