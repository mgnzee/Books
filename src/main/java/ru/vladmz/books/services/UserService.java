package ru.vladmz.books.services;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import ru.vladmz.books.DTOs.bookshelf.BookshelfResponse;
import ru.vladmz.books.DTOs.user.UserChangeEmailRequest;
import ru.vladmz.books.DTOs.user.UserResponse;
import ru.vladmz.books.DTOs.user.UserPatchRequest;
import ru.vladmz.books.entities.Bookshelf;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.exceptions.FileStorageException;
import ru.vladmz.books.exceptions.UserNotFoundException;
import ru.vladmz.books.mappers.UserMapper;
import ru.vladmz.books.repositories.BookshelfRepository;
import ru.vladmz.books.repositories.UserRepository;
import ru.vladmz.books.security.PermissionChecker;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class UserService implements DeletableChecker {

    private final UserRepository repository;
    private final BookshelfRepository bookshelfRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionChecker permissionChecker;
    private final StorageService storageService;

    @Autowired
    public UserService(UserRepository repository, BookshelfRepository bookshelfRepository, PasswordEncoder passwordEncoder, PermissionChecker permissionChecker, StorageService storageService) {
        this.repository = repository;
        this.bookshelfRepository = bookshelfRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionChecker = permissionChecker;
        this.storageService = storageService;
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
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        checkDeleted(user);
        return bookshelfRepository.findByAuthorId(id).stream().map(BookshelfResponse::new).toList();
    }

    public UserResponse createUser(@NonNull User user, String rawPassword, MultipartFile file){
        user.setPassword(passwordEncoder.encode(rawPassword));
        User newUser = repository.save(user);
        generateDefaultBookshelf(newUser);

        if (file != null && !file.isEmpty()) uploadPicture(user, file);

        return UserMapper.toResponse(newUser);
    }

    private void generateDefaultBookshelf(User user){
        Bookshelf newBookshelf = new Bookshelf();
        newBookshelf.setTitle("My Library");
        newBookshelf.setDescription("This is default bookshelf created automatically");
        newBookshelf.setAuthor(user);
        bookshelfRepository.save(newBookshelf);
    }

    public UserResponse updateUser(UserPatchRequest request, Integer id){
        User currentUser = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        permissionChecker.checkPermission(currentUser);
        checkDeleted(currentUser);
        return UserMapper.toResponse(UserMapper.patchUser(currentUser, request));
    }

    public UserResponse changePicture(Integer userId, MultipartFile file){
        User currentUser = repository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        permissionChecker.checkPermission(currentUser);
        checkDeleted(currentUser);

        uploadPicture(currentUser, file);

        return UserMapper.toResponse(currentUser);
    }

    private void uploadPicture(User user, MultipartFile file){
        String fileName = generateFileName(user.getId(), file.getOriginalFilename());
        uploadPictureToStorage(file, fileName);
        user.setProfilePicture(fileName);
    }

    private String generateFileName(Integer userId, String originalName){
        return "avatars/" + userId + "_" + UUID.randomUUID() + "_" + originalName;
    }

    private void uploadPictureToStorage(MultipartFile file, String fileName){
        try {
            storageService.upload(file.getInputStream(), fileName, file.getContentType());
        } catch (IOException | S3Exception ex) {
            throw new FileStorageException("Could not upload avatar", "avatars" ,fileName, ex);
        }
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email){
        return repository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    public UserResponse updateEmail(UserChangeEmailRequest request, Integer userId){
        User user = repository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        permissionChecker.checkPermission(user);
        checkDeleted(user);
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        return UserMapper.toResponse(user);
    }

    public void deleteUser(Integer id){
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        permissionChecker.checkPermission(user);
        checkDeleted(user);
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

    public void hardDelete(Integer id){
        User user = repository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        permissionChecker.checkPermission(user);
        repository.delete(user);
    }
}