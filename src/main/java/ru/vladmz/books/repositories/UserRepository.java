package ru.vladmz.books.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vladmz.books.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

}
