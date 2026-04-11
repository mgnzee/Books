package ru.vladmz.books.services;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladmz.books.DTOs.FileUploadRequest;
import ru.vladmz.books.DTOs.bookshelf.BookshelfResponse;
import ru.vladmz.books.DTOs.user.UserChangeEmailRequest;
import ru.vladmz.books.DTOs.user.UserPatchRequest;
import ru.vladmz.books.DTOs.user.UserResponse;
import ru.vladmz.books.entities.Bookshelf;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.etc.StorageDirectory;
import ru.vladmz.books.exceptions.UserNotFoundException;
import ru.vladmz.books.mappers.UserMapper;
import ru.vladmz.books.repositories.BookshelfRepository;
import ru.vladmz.books.repositories.UserRepository;
import ru.vladmz.books.security.PermissionChecker;

import java.util.List;

@Service
@Transactional
public class UserService implements DeletableChecker {

    private final UserRepository userRepository;
    private final BookshelfRepository bookshelfRepository;
    private final PasswordEncoder passwordEncoder;
    private final PermissionChecker permissionChecker;
    private final FileService fileService;

    @Autowired
    public UserService(UserRepository repository, BookshelfRepository bookshelfRepository,
                       PasswordEncoder passwordEncoder, PermissionChecker permissionChecker, FileService fileService) {
        this.userRepository = repository;
        this.bookshelfRepository = bookshelfRepository;
        this.passwordEncoder = passwordEncoder;
        this.permissionChecker = permissionChecker;
        this.fileService = fileService;
    }

    private User validateUser(Integer userId){
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        permissionChecker.checkPermission(currentUser);
        checkDeleted(currentUser);

        return currentUser;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> findAll(boolean isDeleted, boolean isDisabled){
        return userRepository.findAllByIsDeletedAndIsDisabled(isDeleted, isDisabled)
                .stream().map(UserMapper::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public UserResponse findById(Integer userId){
        return UserMapper.toResponse(userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId)));
    }

    @Transactional(readOnly = true)
    public List<BookshelfResponse> findBookshelvesOfUser(Integer userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        checkDeleted(user);
        return bookshelfRepository.findByAuthorId(userId).stream().map(BookshelfResponse::new).toList();
    }

    public UserResponse createUser(@NonNull User user, String rawPassword){
        user.setPassword(passwordEncoder.encode(rawPassword));
        User newUser = userRepository.save(user);
        generateDefaultBookshelf(newUser);

        return UserMapper.toResponse(newUser);
    }

    private void generateDefaultBookshelf(User user){
        Bookshelf newBookshelf = new Bookshelf();
        newBookshelf.setTitle("My Library");
        newBookshelf.setDescription("This is default bookshelf created automatically");
        newBookshelf.setAuthor(user);
        bookshelfRepository.save(newBookshelf);
    }

    public UserResponse updateUser(UserPatchRequest request, Integer userId){
        User currentUser = validateUser(userId);
        return UserMapper.toResponse(UserMapper.patchUser(currentUser, request));
    }

    public UserResponse changePicture(Integer userId, FileUploadRequest file){
        User currentUser = validateUser(userId);

        uploadPicture(currentUser, file);

        return UserMapper.toResponse(currentUser);
    }

    private void uploadPicture(User user, FileUploadRequest file){
        fileService.deleteResource(user.getProfilePicture(), StorageDirectory.AVATAR);
        String path = fileService.uploadResource(user.getId(), StorageDirectory.AVATAR, file);
        user.setProfilePicture(path);
    }

    public void deletePicture(Integer userId){
        User currentUser = validateUser(userId);

        String currentPicture = currentUser.getProfilePicture();
        if (currentPicture == null) return;

        currentUser.setProfilePicture(null);
        userRepository.saveAndFlush(currentUser);

        fileService.deleteResource(currentPicture, StorageDirectory.AVATAR);
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException(email));
    }

    public UserResponse updateEmail(UserChangeEmailRequest request, Integer userId){
        User user = validateUser(userId);
        if (request.getEmail() != null) user.setEmail(request.getEmail());
        return UserMapper.toResponse(user);
    }

    public void deleteUser(Integer userId){
        User user = validateUser(userId);
        user.setDeleted(true);
    }

    public UserResponse restoreUser(Integer userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException(userId));
        user.setDeleted(false);
        return UserMapper.toResponse(user);
    }

    public UserResponse disableUser(Integer id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setDisabled(true);
        return UserMapper.toResponse(user);
    }

    public UserResponse enableUser(Integer id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setDisabled(false);
        return UserMapper.toResponse(user);
    }

    public void hardDelete(Integer id){
        User user = userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        permissionChecker.checkPermission(user);
        fileService.deleteResource(user.getProfilePicture(), StorageDirectory.AVATAR);
        userRepository.delete(user);
    }
}