package ru.vladmz.books.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import ru.vladmz.books.DTOs.post.PostPatchRequest;
import ru.vladmz.books.DTOs.post.PostResponse;
import ru.vladmz.books.entities.Post;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.exceptions.PostNotFoundException;
import ru.vladmz.books.repositories.PostDao;
import ru.vladmz.books.security.CurrentUserProvider;
import ru.vladmz.books.security.PermissionChecker;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @Mock
    private PostDao postDao;

    @Mock
    private PermissionChecker permissionChecker;

    @Mock
    private CurrentUserProvider provider;

    @InjectMocks
    private PostService postService;

    Post post;
    Integer postId;
    User owner;
    Integer ownerId;

    @BeforeEach
    void setUp(){
        postId = 1;
        ownerId = 10;
        post = new Post();
        owner = new User();
        owner.setId(ownerId);
        post.setId(postId);
        post.setUser(owner);
        post.setTitle("New post");
        post.setText("Post text");
    }

    @Test
    void findById(){
        when(postDao.findById(postId)).thenReturn(Optional.of(post));

        PostResponse result = postService.findById(postId);

        assertNotNull(result);
        assertEquals(result.id(), post.getId());
        assertEquals(result.text(), post.getText());
        assertEquals(result.user().getId(), post.getOwner().getId());
        verify(postDao, times(1)).findById(postId);
    }

    @Test
    void findById_shouldThrowPostNotFound(){
        Integer unexisted = 1000;
        when(postDao.findById(unexisted)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.findById(unexisted));
        verify(postDao, times(1)).findById(unexisted);
    }

    @Test
    void findAll(){
        when(postDao.findAll()).thenReturn(List.of(post, post));

        List<PostResponse> result = postService.findAll();

        assertEquals(2, result.size());
        assertEquals(postId, result.get(0).id());
        verify(postDao, times(1)).findAll();
    }

    @Test
    void findAll_shouldReturnEmptyList(){
        when(postDao.findAll()).thenReturn(List.of());

        List<PostResponse> result = postService.findAll();

        assertTrue(result.isEmpty());
        verify(postDao, times(1)).findAll();
    }

    @Test
    void savePost(){
        when(provider.get()).thenReturn(owner);
        PostResponse result = postService.savePost(post);

        assertNotNull(result);
        assertEquals(postId, result.id());

        ArgumentCaptor<Post> captor = ArgumentCaptor.forClass(Post.class);
        verify(postDao).save(captor.capture());

        Post savedPost = captor.getValue();
        assertEquals(owner, savedPost.getOwner());
        verify(postDao, times(1)).save(post);
    }

    @Test
    void updatePost(){
        PostPatchRequest request = new PostPatchRequest();
        request.setText("Updated text");
        when(postDao.findById(postId)).thenReturn(Optional.of(post));

        PostResponse result = postService.updatePost(request, postId);

        assertEquals("Updated text", result.text());
        verify(postDao, times(1)).update(post);
        verify(permissionChecker, times(1)).checkPermission(post);
    }

    @Test
    void updatePost_shouldThrowPostNotFound(){
        PostPatchRequest request = new PostPatchRequest();
        request.setText("Updated text");
        Integer unexisted = 1000;
        when(postDao.findById(unexisted)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.updatePost(request, unexisted));
        verify(postDao, never()).update(any());
        verify(permissionChecker, never()).checkPermission(any());
    }

    @Test
    void updatePost_shouldThrowAccessDenied(){
        PostPatchRequest request = new PostPatchRequest();
        request.setText("Updated text");
        when(postDao.findById(postId)).thenReturn(Optional.of(post));

        doThrow(new AccessDeniedException("Forbidden"))
                .when(permissionChecker).checkPermission(post);

        assertThrows(AccessDeniedException.class, () -> postService.updatePost(request, postId));

        verify(postDao, never()).update(any());
    }

    @Test
    void deletePost(){
        when(postDao.findById(postId)).thenReturn(Optional.of(post));

        postService.deletePost(postId);

        verify(postDao, times(1)).delete(post);
        verify(permissionChecker, times(1)).checkPermission(post);
    }

    @Test
    void deletePost_shouldThrowPostNotFound(){
        when(postDao.findById(postId)).thenReturn(Optional.empty());

        assertThrows(PostNotFoundException.class, () -> postService.deletePost(postId));

        verify(postDao, never()).delete(any());
        verify(permissionChecker, never()).checkPermission(any());
    }

    @Test
    void deletePost_shouldThrowAccessDenied(){
        when(postDao.findById(postId)).thenReturn(Optional.of(post));

        doThrow(new AccessDeniedException("Forbidden"))
                .when(permissionChecker).checkPermission(post);

        assertThrows(AccessDeniedException.class, () -> postService.deletePost(postId));

        verify(postDao, never()).delete(any());
    }
}