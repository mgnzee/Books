package ru.vladmz.books.services;

import jakarta.transaction.Transactional;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vladmz.books.DTOs.BookshelfResponse;
import ru.vladmz.books.DTOs.UserResponse;
import ru.vladmz.books.DTOs.UserUpdateRequest;
import ru.vladmz.books.entities.Bookshelf;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.exceptions.UserNotFoundException;
import ru.vladmz.books.mappers.UserMapper;
import ru.vladmz.books.repositories.BookshelfRepository;
import ru.vladmz.books.repositories.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;
    private final BookshelfRepository bookshelfRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository repository, BookshelfRepository bookshelfRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.bookshelfRepository = bookshelfRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UserResponse> findAll(boolean isDeleted, boolean isDisabled){
        return repository.findAllByIsDeletedAndIsDisabled(isDeleted, isDisabled)
                .stream().map(UserMapper::toResponse).toList();
    }

    public UserResponse findById(Integer id){
        return UserMapper.toResponse(repository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Transactional
    public List<BookshelfResponse> findBookshelvesOfUser(Integer id){
        repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return bookshelfRepository.findByAuthorId(id).stream().map(BookshelfResponse::new).toList();
    }

    @Transactional
    public UserResponse createUser(@NonNull User user, String rawPassword){
        user.setPassword(passwordEncoder.encode(rawPassword));
        User newUser = repository.save(user);
        Bookshelf newBookshelf = new Bookshelf();
        newBookshelf.setTitle("My Library");
        newBookshelf.setDescription("This is default bookshelf created automatically");
        newBookshelf.setAuthor(newUser);
        bookshelfRepository.save(newBookshelf);
        return UserMapper.toResponse(newUser);
    }

    @Transactional
    public UserResponse updateUser(UserUpdateRequest request, Integer id){
        User currentUser = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return UserMapper.toResponse(repository.save(UserMapper.patchUser(currentUser, request)));
    }

    public User findByEmail(String email){
        return repository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    @Transactional
    public void deleteUser(Integer id){
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setDeleted(true);
        repository.save(user);
    }

    @Transactional
    public UserResponse restoreUser(Integer id){
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setDeleted(false);
        return UserMapper.toResponse(repository.save(user));
    }

    @Transactional
    public UserResponse disableUser(Integer id){
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setDisabled(true);
        return UserMapper.toResponse(repository.save(user));
    }

    @Transactional
    public UserResponse enableUser(Integer id){
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setDisabled(false);
        return UserMapper.toResponse(repository.save(user));
    }

    @Transactional
    public void deleteById(Integer id){
        repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        repository.deleteById(id);
    }
}
