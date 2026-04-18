package ru.vladmz.books.repositories;

import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.vladmz.books.entities.Bookshelf;

import java.util.List;

@Repository
public interface BookshelfRepository extends JpaRepository<Bookshelf, Integer> {

    List<Bookshelf> findByAuthorId(Integer authorId);

    @EntityGraph(attributePaths = {"author"})
    @NonNull
    Page<Bookshelf> findAll(@NonNull Pageable pageable);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(value = "DELETE FROM bookshelves_to_books WHERE bookshelf_id = :bookshelfId AND book_id = :bookId", nativeQuery = true)
    void removeBookFromBookshelf(Integer bookshelfId, Integer bookId);
}
