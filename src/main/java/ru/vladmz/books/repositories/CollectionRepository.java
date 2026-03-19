package ru.vladmz.books.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vladmz.books.entities.Collection;

@Repository
public interface CollectionRepository extends JpaRepository<Collection, Integer> {
}
