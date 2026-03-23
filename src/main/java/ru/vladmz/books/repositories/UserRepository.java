package ru.vladmz.books.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.vladmz.books.entities.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    List<User> findAllByIsDeletedAndIsDisabled(boolean isDeleted, boolean isDisabled);

    Optional<User> findByIdAndIsDeletedAndIsDisabled(Integer id, boolean isDeleted, boolean isDisabled);
}
