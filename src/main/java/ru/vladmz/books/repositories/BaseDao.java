package ru.vladmz.books.repositories;

import java.util.List;
import java.util.Optional;

public interface BaseDao<T, ID> {

    Optional<T> findById(ID id);

    List<T> findAll();

    void save(T entity);

    T update(T entity);

    void delete(T entity);

    boolean deleteById(ID id);
}
