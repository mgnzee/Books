package ru.vladmz.books.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.vladmz.books.DTOs.FileUploadRequest;
import ru.vladmz.books.DTOs.bookshelf.BookshelfResponse;
import ru.vladmz.books.DTOs.user.UserChangeEmailRequest;
import ru.vladmz.books.DTOs.user.UserCreateRequest;
import ru.vladmz.books.DTOs.user.UserResponse;
import ru.vladmz.books.DTOs.user.UserPatchRequest;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.mappers.UserMapper;
import ru.vladmz.books.services.FollowerService;
import ru.vladmz.books.services.UserService;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;
    private final FollowerService followerService;

    @Autowired
    public UserController(UserService service, FollowerService followerService) {
        this.service = service;
        this.followerService = followerService;
    }

    //TODO: ADD JWT TO RESPONSE
    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestPart @Valid UserCreateRequest request) throws IOException {
        User user = UserMapper.toUser(request);
        UserResponse created = service.createUser(user, request.getRawPassword());

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();

        return ResponseEntity.created(location).body(created);
    }

    @GetMapping
    public List<UserResponse> selectAll(){
        return service.findAll(false, false);
    }

    @GetMapping("/deleted")
    public List<UserResponse> selectDeleted(){
        return service.findAll(true, false);
    }

    @GetMapping("/disabled")
    public List<UserResponse> selectDisabled(){
        return service.findAll(false, true);
    }

    @GetMapping("/{id}")
    public UserResponse selectById(@PathVariable Integer id){
        return service.findById(id);
    }

    @GetMapping("/{id}/bookshelves")
    public List<BookshelfResponse> selectBookshelves(@PathVariable Integer id){
        return service.findBookshelvesOfUser(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Integer id, @RequestBody UserPatchRequest request){
        UserResponse updated = service.updateUser(request, id);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PatchMapping("/{id}/email")
    public ResponseEntity<UserResponse> updateEmail(@PathVariable Integer id, @RequestBody UserChangeEmailRequest request){
        UserResponse updated = service.updateEmail(request, id);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
    }

    @PatchMapping("/{id}/profile-picture")
    public ResponseEntity<UserResponse> changePicture(@PathVariable Integer id, @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) return ResponseEntity.badRequest().build();
        var request = new FileUploadRequest(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
        UserResponse updated = service.changePicture(id, request);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id){
        service.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PatchMapping("/{id}/restore")
    public ResponseEntity<UserResponse> restoreUser(@PathVariable Integer id){
        return ResponseEntity.ok(service.restoreUser(id));
    }

    @PatchMapping("/{id}/disable")
    public ResponseEntity<UserResponse> disableUser(@PathVariable Integer id){
        return ResponseEntity.ok(service.disableUser(id));
    }

    @PatchMapping("/{id}/enable")
    public ResponseEntity<UserResponse> enableUser(@PathVariable Integer id){
        return ResponseEntity.ok(service.enableUser(id));
    }

    //TODO: ADD CASCADE DELETION
    @DeleteMapping("/{id}/permanent")
    public ResponseEntity<Void> deletePermanently(@PathVariable Integer id){
        service.hardDelete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{id}/profile-picture")
    public ResponseEntity<Void> deletePicture(@PathVariable Integer id){
        service.deletePicture(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    // -----FOLLOWERS----- //
    @GetMapping("/{id}/followers")
    public List<UserResponse> getFollowersById(@PathVariable Integer id){
        return followerService.getFollowersByUserId(id);
    }

    @PostMapping("/{id}/followers")
    public ResponseEntity<Void> follow(@PathVariable Integer id){
        followerService.subscribe(id);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/followers")
    public ResponseEntity<Void> unfollow(@PathVariable Integer id){
        followerService.unsubscribe(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}