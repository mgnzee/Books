package ru.vladmz.books.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.vladmz.books.DTOs.bookshelf.BookshelfResponse;
import ru.vladmz.books.DTOs.user.UserChangeEmailRequest;
import ru.vladmz.books.DTOs.user.UserPatchRequest;
import ru.vladmz.books.DTOs.user.UserResponse;
import ru.vladmz.books.entities.Bookshelf;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.exceptions.ResourceAlreadyDeletedException;
import ru.vladmz.books.exceptions.UserNotFoundException;
import ru.vladmz.books.repositories.UserRepository;
import ru.vladmz.books.security.PermissionChecker;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PermissionChecker permissionChecker;

    @Mock
    private FileService fileService;

    @Mock
    private BookshelfService bookshelfService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService service;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Petya");
        user.setEmail("petya@example.com");
        user.setDeleted(false);
        user.setDisabled(false);
    }

    @Test
    void findAll() {
        when(userRepository.findAllByIsDeletedAndIsDisabled(false, false)).thenReturn(List.of(user));

        List<UserResponse> result = service.findAll(false, false);

        assertEquals(1, result.size());
        assertEquals(user.getId(), result.getFirst().getId());
        verify(userRepository, times(1)).findAllByIsDeletedAndIsDisabled(false, false);
    }

    @Test
    void findAll_ShouldReturnEmptyList(){
        when(userRepository.findAllByIsDeletedAndIsDisabled(false, false)).thenReturn(Collections.emptyList());

        List<UserResponse> result = service.findAll(false, false);

        assertTrue(result.isEmpty());
        verify(userRepository, times(1)).findAllByIsDeletedAndIsDisabled(false, false);
    }

    @Test
    void findById(){
        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        UserResponse result = service.findById(1);

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void findById_shouldThrowUserNotFound(){
        int wrongId = 10;
        when(userRepository.findById(wrongId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.findById(wrongId));
    }

    @Test
    void findByEmail(){
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        User result = service.findByEmail(user.getEmail());

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void findByEmail_shouldThrowUserNotFound(){
        String wrongEmail = "Wrong";
        when(userRepository.findByEmail(wrongEmail)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.findByEmail(wrongEmail));
    }

    @Test
    void findBookshelvesOfUser(){
        BookshelfResponse bookshelf = generateBookshelf();
        when(bookshelfService.findByUserId(user.getId())).thenReturn(List.of(bookshelf));
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        List<BookshelfResponse> result = service.findBookshelvesOfUser(user.getId());

        assertEquals(1, result.size());
        assertEquals(bookshelf.getTitle(), result.getFirst().getTitle());
    }

    @Test
    void findBookshelvesOfUser_shouldThrowUserNotFound(){
        int wrongId = 10;
        when(userRepository.findById(wrongId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.findBookshelvesOfUser(wrongId));
        verify(bookshelfService, never()).findByUserId(any());
    }

    @Test
    void findBookshelvesOfUser_shouldThrowResourceAlreadyDeleted(){
        user.setDeleted(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        assertThrows(ResourceAlreadyDeletedException.class, () -> service.findBookshelvesOfUser(user.getId()));

        verify(bookshelfService, never()).findByUserId(any());
    }

    private BookshelfResponse generateBookshelf(){
        var bookshelf = new Bookshelf("New shelf", "This is my personal book collection", "cover.png");
        bookshelf.setAuthor(user);
        return new BookshelfResponse(bookshelf);
    }

    @Test
    void createUser(){
        String rawPassword = "raw";
        String encodedPassword = "encoded";

        when(userRepository.save(any(User.class))).thenReturn(user);
        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);

        UserResponse response = service.createUser(user, rawPassword);

        assertNotNull(response);
        assertEquals(user.getId(), response.getId());
        assertEquals(user.getEmail(), response.getEmail());
        assertEquals(encodedPassword, user.getPassword());

        verify(passwordEncoder, times(1)).encode(rawPassword);
        verify(bookshelfService, times(1)).generateDefaultBookshelf(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void updateUser(){
        String newName = "New name";
        var request = new UserPatchRequest(newName);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserResponse response = service.updateUser(request, user.getId());

        assertEquals(newName, response.getName());
        assertEquals(user.getEmail(), response.getEmail());
    }

    @Test
    void updateEmail(){
        String newEmail = "new@mail.com";
        var request = new UserChangeEmailRequest(newEmail);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserResponse response = service.updateEmail(request, user.getId());

        assertEquals(newEmail, response.getEmail());
        assertEquals(user.getId(), response.getId());
    }

    @Test
    void deleteUser(){
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        service.deleteUser(user.getId());

        assertTrue(user.isDeleted());
    }

    @Test
    void deleteUser_shouldThrowAccessDenied(){
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        doThrow(new AccessDeniedException("Forbidden")).when(permissionChecker).checkPermission(user);

        assertThrows(AccessDeniedException.class, () -> service.deleteUser(user.getId()));
        assertFalse(user.isDeleted());
    }

    @Test
    void restoreUser(){
        user.setDeleted(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        service.restoreUser(user.getId());

        assertFalse(user.isDeleted());
    }

    @Test
    void restoreUser_shouldThrowUserNotFound(){
        user.setDeleted(true);
        int wrongId = 10;
        when(userRepository.findById(wrongId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.restoreUser(wrongId));
        assertTrue(user.isDeleted());
    }

    @Test
    void disableUser(){
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        service.disableUser(user.getId());
        assertTrue(user.isDisabled());
    }

    @Test
    void disableUser_shouldThrowUserNotFound(){
        user.setDisabled(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.disableUser(user.getId()));
        assertTrue(user.isDisabled());
    }

    @Test
    void enableUser(){
        user.setDisabled(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        service.enableUser(user.getId());

        assertTrue(user.isEnabled());
    }

    @Test
    void enableUser_shouldThrowUserNotFound(){
        user.setDisabled(true);
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.enableUser(user.getId()));
        assertFalse(user.isEnabled());
    }

    @Test
    void hardDelete(){
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        service.hardDelete(user.getId());
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void hardDelete_shouldThrowUserNotFound(){
        int wrongId = 10;
        when(userRepository.findById(wrongId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> service.hardDelete(wrongId));
        verify(userRepository, never()).delete(any());
    }

    @Test
    void hardDelete_shouldThrowAccessDenied(){
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        doThrow(new AccessDeniedException("Forbidden")).when(permissionChecker).checkPermission(user);

        assertThrows(AccessDeniedException.class, () -> service.hardDelete(user.getId()));
        verify(userRepository, never()).delete(any());
        verify(fileService, never()).deleteResource(any(), any());
    }
}