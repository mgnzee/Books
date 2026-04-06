package ru.vladmz.books.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vladmz.books.DTOs.post.PostPatchRequest;
import ru.vladmz.books.DTOs.post.PostRequest;
import ru.vladmz.books.DTOs.post.PostResponse;
import ru.vladmz.books.mappers.PostMapper;
import ru.vladmz.books.services.PostService;

import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping
    public List<PostResponse> findAll(){
        return postService.findAll();
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponse> findById(@PathVariable Integer postId){
        return ResponseEntity.ok(postService.findById(postId));
    }

    @PostMapping
    public ResponseEntity<PostResponse> createPost(@RequestBody @Valid PostRequest request){
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.savePost(PostMapper.toPost(request)));
    }

    @PatchMapping("/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable Integer postId, @RequestBody @Valid PostPatchRequest request){
        return ResponseEntity.ok(postService.updatePost(request, postId));
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer postId){
        postService.deletePost(postId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
