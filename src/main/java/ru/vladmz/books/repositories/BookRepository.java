package ru.vladmz.books.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.vladmz.books.entities.Book;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> { //TODO: FIX N+1!!!!!!!!!

    @EntityGraph(attributePaths = {"uploadedBy"})
    Page<Book> findAll(Pageable pageable);

    @Query("select b from Book b join fetch b.genres g where g.id = :genreId")
    Page<Book> findAllByGenreId(Integer genreId, Pageable pageable);

    @EntityGraph(attributePaths = {"uploadedBy"})
    Page<Book> findBooksByBookshelves_Id(Integer id, Pageable pageable);
}
