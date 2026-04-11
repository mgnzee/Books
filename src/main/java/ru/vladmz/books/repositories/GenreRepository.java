package ru.vladmz.books.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vladmz.books.entities.Genre;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Integer> {


}
