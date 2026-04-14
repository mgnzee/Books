package ru.vladmz.books.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vladmz.books.DTOs.user.UserResponse;
import ru.vladmz.books.entities.Follower;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.exceptions.AlreadySubscribedException;
import ru.vladmz.books.exceptions.SelfSubscriptionException;
import ru.vladmz.books.exceptions.UserNotFoundException;
import ru.vladmz.books.repositories.FollowerRepository;
import ru.vladmz.books.repositories.UserRepository;
import ru.vladmz.books.security.CurrentUserProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FollowerServiceTest {

    @Mock
    private FollowerRepository followerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CurrentUserProvider provider;

    @InjectMocks
    private FollowerService followerService;

    User follower;
    User current;

    @BeforeEach
    void setUp(){
        follower = new User();
        follower.setId(5);
        current = new User();
        current.setId(10);
    }

    @Test
    void getFollowersByUserId(){
        when(userRepository.existsById(1)).thenReturn(true);
        when(userRepository.findAllFollowers(1)).thenReturn(List.of(follower));

        List<UserResponse> result = followerService.getFollowersByUserId(1);

        assertEquals(1, result.size());
        assertEquals(follower.getId(), result.get(0).getId());
        assertEquals(follower.getName(), result.get(0).getName());
        verify(userRepository, times(1)).findAllFollowers(1);
    }

    @Test
    void getFollowersByUserId_shouldThrowUserNotFoundException(){
        when(userRepository.existsById(1)).thenReturn(false);

        assertThrows(UserNotFoundException.class, () -> followerService.getFollowersByUserId(1));
        verify(userRepository, never()).findAllFollowers(any());
    }

    @Test
    void subscribe(){
        when(userRepository.existsById(5)).thenReturn(true);
        when(userRepository.getReferenceById(5)).thenReturn(follower);
        when(provider.get()).thenReturn(current);

        Follower follow = new Follower(follower, current);
        followerService.subscribe(5);

        verify(followerRepository, times(1)).save(any(Follower.class));
    }

    @Test
    void subscribe_shouldThrowUserNotFoundException(){
        when(userRepository.existsById(100)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> followerService.subscribe(100));
        verify(followerRepository, never()).save(any(Follower.class));
    }

    @Test
    void subscribe_shouldThrowSelfSubscriptionException(){
        when(userRepository.existsById(5)).thenReturn(true);
        when(provider.get()).thenReturn(follower);

        assertThrows(SelfSubscriptionException.class, () -> followerService.subscribe(5));
        verify(followerRepository, never()).save(any(Follower.class));
    }

    @Test
    void subscribe_shouldThrowAlreadySubscribedException(){
        when(userRepository.existsById(5)).thenReturn(true);
        when(provider.get()).thenReturn(current);
        when(followerRepository.existsByUserIdAndFollowerId(5, 10)).thenReturn(true);

        assertThrows(AlreadySubscribedException.class, () -> followerService.subscribe(5));
        verify(followerRepository, never()).save(any(Follower.class));
    }

    @Test
    void unsubscribe(){
        when(userRepository.existsById(5)).thenReturn(true);
        when(provider.get()).thenReturn(current);

        followerService.unsubscribe(5);
        verify(followerRepository, times(1)).deleteByUserIdAndFollowerId(5, 10);
    }

    @Test
    void unsubscribe_shouldThrowUserNotFoundException(){
        when(userRepository.existsById(100)).thenReturn(false);
        assertThrows(UserNotFoundException.class, () -> followerService.unsubscribe(100));
        verify(followerRepository, never()).delete(any(Follower.class));
    }
}
