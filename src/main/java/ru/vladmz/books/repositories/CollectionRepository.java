package ru.vladmz.books.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vladmz.books.DTOs.CollectionResponse;
import ru.vladmz.books.entities.Collection;

import java.util.List;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Integer> {

    List<Collection> findByAuthorId(Integer authorId);

}
