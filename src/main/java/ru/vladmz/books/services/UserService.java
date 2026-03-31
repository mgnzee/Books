package ru.vladmz.books.services;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladmz.books.DTOs.bookshelf.BookshelfResponse;
import ru.vladmz.books.DTOs.user.UserChangeEmailRequest;
import ru.vladmz.books.DTOs.user.UserResponse;
import ru.vladmz.books.DTOs.user.UserPatchRequest;
import ru.vladmz.books.entities.Bookshelf;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.exceptions.UserNotFoundException;
import ru.vladmz.books.mappers.UserMapper;
import ru.vladmz.books.repositories.BookshelfRepository;
import ru.vladmz.books.repositories.UserRepository;
import ru.vladmz.books.security.PermissionChecker;

import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository repository;
    private final BookshelfRepository bookshelfRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionChecker permissionChecker;

    @Autowired
    public UserService(UserRepository repository, BookshelfRepository bookshelfRepository, PasswordEncoder passwordEncoder, PermissionChecker permissionChecker) {
        this.repository = repository;
        this.bookshelfRepository = bookshelfRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionChecker = permissionChecker;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll(boolean isDeleted, boolean isDisabled){
        return repository.findAllByIsDeletedAndIsDisabled(isDeleted, isDisabled)
                .stream().map(UserMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Integer id){
        return UserMapper.toResponse(repository.findById(id).orElseThrow(() -> new UserNotFoundException(id)));
    }

    @Transactional(readOnly = true)
    public List<BookshelfResponse> findBookshelvesOfUser(Integer id){
        repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        return bookshelfRepository.findByAuthorId(id).stream().map(BookshelfResponse::new).toList();
    }

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

    public UserResponse updateUser(UserPatchRequest request, Integer id){
        User currentUser = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        permissionChecker.checkPermission(currentUser);
        return UserMapper.toResponse(UserMapper.patchUser(currentUser, request));
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email){
        return repository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    public UserResponse updateEmail(UserChangeEmailRequest request, Integer userId){
        User user = repository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        permissionChecker.checkPermission(user);
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        return UserMapper.toResponse(user);
    }

    public void deleteUser(Integer id){
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        permissionChecker.checkPermission(user);
        user.setDeleted(true);
    }

    public UserResponse restoreUser(Integer id){
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setDeleted(false);
        return UserMapper.toResponse(user);
    }

    public UserResponse disableUser(Integer id){
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setDisabled(true);
        return UserMapper.toResponse(user);
    }

    public UserResponse enableUser(Integer id){
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setDisabled(false);
        return UserMapper.toResponse(user);
    }

    public void deleteById(Integer id){
        repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        repository.deleteById(id);
    }
}