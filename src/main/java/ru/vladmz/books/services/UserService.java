package ru.vladmz.books.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.vladmz.books.DTOs.BookshelfResponse;
import ru.vladmz.books.DTOs.UserResponse;
import ru.vladmz.books.entities.Bookshelf;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.exceptions.UserNotFoundException;
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
                .stream().map(UserResponse::new).toList();
    }

    public UserResponse findById(Integer id){
        return new UserResponse(repository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Transactional
    public List<BookshelfResponse> findBookshelvesOfUser(Integer id){
        repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return bookshelfRepository.findByAuthorId(id).stream().map(BookshelfResponse::new).toList();
    }

    @Transactional
    public UserResponse createUser(User user, String rawPassword){
        user.setPassword(passwordEncoder.encode(rawPassword));
        User newUser = repository.save(user);
        Bookshelf newBookshelf = new Bookshelf();
        newBookshelf.setTitle("My Library");
        newBookshelf.setDescription("This is default bookshelf created automatically");
        newBookshelf.setAuthor(newUser);
        bookshelfRepository.save(newBookshelf);
        return new UserResponse(newUser);
    }

    @Transactional
    public UserResponse updateUser(User user, Integer id){
        User currentUser = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if(user.getName() != null) currentUser.setName(user.getName());
        if(user.getEmail() != null) currentUser.setEmail(user.getEmail());
        if(user.getProfilePicture() != null) currentUser.setProfilePicture(user.getProfilePicture());
        return new UserResponse(repository.save(currentUser));
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
        return new UserResponse(repository.save(user));
    }

    @Transactional
    public UserResponse disableUser(Integer id){
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setDisabled(true);
        return new UserResponse(repository.save(user));
    }

    @Transactional
    public UserResponse enableUser(Integer id){
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setDisabled(false);
        return new UserResponse(repository.save(user));
    }

    @Transactional
    public void deleteById(Integer id){
        repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        repository.deleteById(id);
    }
}
