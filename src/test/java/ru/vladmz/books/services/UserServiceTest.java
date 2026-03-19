package ru.vladmz.books.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.repositories.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private UserService service;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1);
        user.setName("Petya");
        user.setEmail("petya@example.com");
        user.setProfilePicture("http://example.com/pic.jpg");
    }

    @Test
    void findAllUsers() {
        List<User> users = Arrays.asList(user);
        when(repository.findAll()).thenReturn(users);

        List<User> result = service.findAll();

        assertEquals(1, result.size());
        assertEquals(user, result.getFirst());
        verify(repository, times(1)).findAll();
    }

    @Test
    void findById() {
        when(repository.findById(1)).thenReturn(Optional.of(user));

        User result = service.findById(1);

        assertNotNull(result);
        assertEquals(user, result);
        verify(repository, times(1)).findById(1);
    }

    @Test
    void shouldThrowExceptionIfDoesntExist() {
        when(repository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> service.findById(99));

        verify(repository, times(1)).findById(99);
    }

    @Test
    void createUser() {
        User newUser = new User();
        newUser.setName("Jane");
        newUser.setEmail("jane@example.com");

        User savedUser = new User();
        savedUser.setId(2);
        savedUser.setName("Jane");
        savedUser.setEmail("jane@example.com");

        when(repository.save(any(User.class))).thenReturn(savedUser);

        User result = service.createUser(newUser);

        assertNotNull(result);
        assertEquals(2, result.getId());
        assertEquals(savedUser, result);
        verify(repository, times(1)).save(any(User.class));
    }

    @Test
    void updateUser() {
        User existingUser = new User();
        existingUser.setId(1);
        existingUser.setName("John");
        existingUser.setEmail("john@example.com");

        User updateData = new User();
        updateData.setName("John Updated");
        updateData.setEmail("john.updated@example.com");

        when(repository.findById(1)).thenReturn(Optional.of(existingUser));
        when(repository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User result = service.updateUser(updateData, 1);

        assertEquals("John Updated", result.getName());
        assertEquals("john.updated@example.com", result.getEmail());
        verify(repository, times(1)).findById(1);
        verify(repository, times(1)).save(existingUser);
    }

    @Test
    void deleteUser() {
        doNothing().when(repository).deleteById(1);
        service.deleteUser(1);
        verify(repository, times(1)).deleteById(1);
    }
}