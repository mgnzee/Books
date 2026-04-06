package ru.vladmz.books.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vladmz.books.DTOs.post.PostPatchRequest;
import ru.vladmz.books.DTOs.post.PostResponse;
import ru.vladmz.books.entities.Post;
import ru.vladmz.books.exceptions.PostNotFoundException;
import ru.vladmz.books.mappers.PostMapper;
import ru.vladmz.books.mappers.UserMapper;
import ru.vladmz.books.repositories.PostDao;
import ru.vladmz.books.security.CurrentUserProvider;
import ru.vladmz.books.security.PermissionChecker;

import java.util.List;

@Service
@Transactional
public class PostService {

    private final PostDao postDao;
    private final PermissionChecker permissionChecker;
    private final CurrentUserProvider provider;

    public PostService(PostDao postDao, PermissionChecker permissionChecker, CurrentUserProvider provider) {
        this.postDao = postDao;
        this.permissionChecker = permissionChecker;
        this.provider = provider;
    }

    @Transactional(readOnly = true)
    public PostResponse findById(Integer id){
        Post post = postDao.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        return PostMapper.toResponse(post, UserMapper.toResponse(post.getOwner()));
    }

    @Transactional(readOnly = true)
    public List<PostResponse> findAll(){
        return postDao.findAll().stream()
                .map(p -> PostMapper.toResponse(p, UserMapper.toResponse(p.getOwner()))).toList();
    }

    public PostResponse savePost(Post post){
        post.setUser(provider.get());
        postDao.save(post);
        return PostMapper.toResponse(post, UserMapper.toResponse(post.getOwner()));
    }

    public PostResponse updatePost(PostPatchRequest request, Integer id){
        Post target = postDao.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        permissionChecker.checkPermission(target);
        Post updated = PostMapper.patchPost(target, request);
        postDao.update(updated);
        return PostMapper.toResponse(updated, UserMapper.toResponse(updated.getOwner()));
    }

    public void deletePost(Integer id){
        Post target = postDao.findById(id).orElseThrow(() -> new PostNotFoundException(id));
        permissionChecker.checkPermission(target);
        postDao.delete(target);
    }
}
