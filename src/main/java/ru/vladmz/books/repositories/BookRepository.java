package ru.vladmz.books.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vladmz.books.entities.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Integer> {



}
