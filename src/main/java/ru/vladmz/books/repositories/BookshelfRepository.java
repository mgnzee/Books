package ru.vladmz.books.repositories;

import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.vladmz.books.entities.Bookshelf;

import java.util.List;

@Repository
public interface BookshelfRepository extends JpaRepository<Bookshelf, Integer> {

    List<Bookshelf> findByAuthorId(Integer authorId);

    @Query("SELECT b FROM Bookshelf b LEFT JOIN FETCH b.author")
    @NonNull
    List<Bookshelf> findAll();

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM bookshelves_to_books WHERE bookshelf_id = :bookshelfId AND book_id = :bookId", nativeQuery = true)
    void removeBookFromBookshelf(Integer bookshelfId, Integer bookId);
}
