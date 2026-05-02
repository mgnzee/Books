package ru.vladmz.books.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.whenever
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.*
import ru.vladmz.books.DTOs.PageParams
import ru.vladmz.books.entities.Book
import ru.vladmz.books.entities.Genre
import ru.vladmz.books.exceptions.GenreNotFoundException
import ru.vladmz.books.security.JwtUtil
import ru.vladmz.books.services.CustomUserDetailsService
import ru.vladmz.books.services.GenreService

@WebMvcTest(controllers = [GenreController::class])
@AutoConfigureMockMvc(addFilters = false)
class GenreControllerTest (@Autowired val mockMvc: MockMvc) {

    private val objectMapper: ObjectMapper = ObjectMapper().registerKotlinModule()

    @MockitoBean
    lateinit var genreService: GenreService
    @MockitoBean
    lateinit var jwtUtil: JwtUtil
    @MockitoBean
    lateinit var customUserDetailsService: CustomUserDetailsService

    @Test
    fun findAll() {
        val genres = listOf(Genre(1, "Classical"), Genre(2, "Sci-Fi"))

        whenever(genreService.findAll()).thenReturn(genres)

        mockMvc.get("/genres"){
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.length()") { value(2) }
            jsonPath("$[0].id") { value(1) }
            jsonPath("$[0].title") { value("Classical") }
        }
    }

    @Test
    fun findById() {
        val genre = Genre(1, "Classical")
        whenever(genreService.findById(genre.id)).thenReturn(genre)

        mockMvc.get("/genres/${genre.id}"){
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.id") { value(genre.id) }
            jsonPath("$.title") { value("Classical") }
        }
    }

    @Test
    fun findById_shouldReturn404() {
        val genreId = 100;
        whenever(genreService.findById(genreId)).thenThrow(GenreNotFoundException(genreId))

        get404("/genres/$genreId", genreId)
    }

    private fun get404(endpoint: String, genreId: Int){
        mockMvc.get(endpoint){
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isNotFound() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.status") { value(404) }
            jsonPath("$.error") { value("Resource not found") }
            jsonPath("$.message") { value("Genre not found with id: $genreId") }
        }
    }

    @Test
    fun findBooksByGenreId(){
        val genreId = 1
        val book1 = Book()
        book1.id = 1
        book1.title = "Crime and punishment"
        book1.genres.add(Genre(1, "Classical"))
        val book2 = Book()
        book2.id = 2
        book2.title = "Le Comte de Monte-Cristo"
        book2.genres.add(Genre(1, "Classical"))
        val books = PageImpl(listOf(book1, book2))

        whenever(genreService.findBooksByGenre(eq(genreId), any())).thenReturn(books)

        mockMvc.get("/genres/$genreId/books"){
            accept(MediaType.APPLICATION_JSON)
        }.andExpect {
            status { isOk() }
            content { contentType(MediaType.APPLICATION_JSON) }
            jsonPath("$.size") { value(books.size) }
            jsonPath("$.content[0].id") { value(1) }
            jsonPath("$.content[0].title") { value("Crime and punishment") }
            jsonPath("$.content[1].id") { value(2) }
            jsonPath("$.content[1].title") { value("Le Comte de Monte-Cristo") }
        }
    }

    @Test
    fun findBooksByGenreId_shouldReturn404() {
        val genreId = 100
        whenever(genreService.findBooksByGenre(eq(genreId), any())).thenThrow(GenreNotFoundException(genreId))

        get404("/genres/$genreId/books", genreId)
    }
}