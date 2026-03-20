package ru.vladmz.books.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vladmz.books.entities.Bookshelf;

import java.util.List;

@Repository
public interface BookshelfRepository extends JpaRepository<Bookshelf, Integer> {

    List<Bookshelf> findByAuthorId(Integer authorId);

}
