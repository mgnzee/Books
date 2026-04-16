package ru.vladmz.books.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.kotlin.anyOrNull
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import ru.vladmz.books.DTOs.FileUploadRequest
import ru.vladmz.books.DTOs.book.BookCreateRequest
import ru.vladmz.books.DTOs.book.BookPatchRequest
import ru.vladmz.books.DTOs.book.BookResponse
import ru.vladmz.books.DTOs.genre.GenreRequest
import ru.vladmz.books.GlobalExceptionHandler
import ru.vladmz.books.entities.Book
import ru.vladmz.books.entities.Genre
import ru.vladmz.books.exceptions.BookNotFoundException
import ru.vladmz.books.mappers.BookMapper
import ru.vladmz.books.security.JwtUtil
import ru.vladmz.books.services.BookService
import ru.vladmz.books.services.CustomUserDetailsService

@WebMvcTest(controllers = [BookController::class, GlobalExceptionHandler::class])
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
        val books = PageImpl(listOf(
            BookMapper.toResponse(book),
            BookMapper.toResponse(book),
            BookMapper.toResponse(book)))
        whenever(bookService.findAll(anyInt(), anyInt(), anyOrNull(), anyOrNull()))
            .thenReturn(books)

        mockMvc.get("/books") {
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.size") { value(books.size) }
            jsonPath("$.content[0].id") { value(book.id) }
            jsonPath("$.content[0].title") { value(book.title) }
        }
    }

    @Test
    fun createBook(){
        val genreId = 5;
        val genreEntity = Genre();
        genreEntity.id = genreId;
        book.genres.add(genreEntity)
        book.author = "Dostoevsky F.M."

        val genres = setOf(GenreRequest(genreId))
        val request = BookCreateRequest(book.title, "Dostoevsky F.M.", "Description", "RU", genres)


        val expectedResponse = BookMapper.toResponse(book)

        whenever(bookService.createBook(BookMapper.toBook(request), genres)).thenReturn(expectedResponse)

        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            jsonPath("$.title") { value(book.title) }
            jsonPath("$.genres[0].id") { value(genreId) }
        }
    }

    @Test
    fun createBook_shouldReturn400(){
        val genreId = 5;
        val blankTitle = ""
        val genres = setOf(GenreRequest(genreId))
        val request = BookCreateRequest(blankTitle, "Dostoevsky F.M.", "Description", "RU", genres)

        mockMvc.post("/books") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") { value ("title : must not be blank; ")}
        }
    }

    @Test
    fun updateBook(){
        val request = BookPatchRequest.builder().title("New title").build();
        val expectedResponse = BookResponse.testTemplate(bookId, request.title)

        whenever(bookService.updateBook(request, bookId)).thenReturn(expectedResponse)

        mockMvc.patch("/books/$bookId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            jsonPath("$.title") { value(request.title) }
        }
    }

    @Test
    fun updateBook_shouldReturn404(){
        val wrongId = 100
        val newTitle = "New Title"
        val request = BookPatchRequest.builder().title(newTitle).build()
        whenever(bookService.updateBook(request, wrongId)).thenThrow(BookNotFoundException(wrongId))

        mockMvc.patch("/books/$wrongId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.status") { value(404) }
            jsonPath("$.error") { value("Resource not found") }
            jsonPath("$.message") { value("Book not found with id: $wrongId") }
        }
    }

    @Test
    fun updateBook_shouldReturn403(){
        val newTitle = "New Title"
        val request = BookPatchRequest.builder().title(newTitle).build()
        whenever(bookService.updateBook(request, bookId))
            .thenThrow(org.springframework.security.access.AccessDeniedException("Forbidden"))

        mockMvc.patch("/books/$bookId") {
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
    fun updateBook_shouldReturn400(){
        val blankTitle = "   "
        val request = BookPatchRequest.builder().title(blankTitle).build()

        mockMvc.patch("/books/$bookId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
            jsonPath("$.message") { value ("title : must not be blank; ")}
        }
    }

    @Test
    fun updateBook_shouldReturn405(){
        val request = BookPatchRequest.builder().title("New title").build();
        val expectedResponse = BookResponse.testTemplate(bookId, request.title)

        whenever(bookService.updateBook(request, bookId)).thenReturn(expectedResponse)

        mockMvc.post("/books/$bookId") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.writeValueAsString(request)
            accept = MediaType.APPLICATION_JSON
        }.andExpect {
            status { isMethodNotAllowed() }
            jsonPath("$.message") { value ("Request method 'POST' is not supported")}
        }
    }

    @Test
    fun updateCover(){

    }

    @Test
    fun updateCover_shouldReturn404(){

    }

    @Test
    fun updateCover_shouldReturn403(){

    }

    @Test
    fun updateCover_shouldReturn400(){

    }

    @Test
    fun updateCover_shouldReturn413(){
        val veryVeryLargeFile = MockMultipartFile("file", "cover.png", "image/png", ByteArray(1))

    }

    @Test
    fun updateCover_shouldReturn409(){

    }

    @Test
    fun deleteCover(){

    }

    @Test
    fun deleteCover_shouldReturn404(){

    }

    @Test
    fun deleteBook(){

    }

    @Test
    fun deleteBook_shouldReturn404(){

    }

    @Test
    fun deleteBook_shouldReturn403(){

    }

    @Test
    fun addBookFile(){

    }

    @Test
    fun addBookFile_shouldReturn404(){

    }

    @Test
    fun addBookFile_shouldReturn403(){

    }
}