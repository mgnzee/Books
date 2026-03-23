package ru.vladmz.books.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import ru.vladmz.books.DTOs.BookshelfResponse;
import ru.vladmz.books.DTOs.UserCreateRequest;
import ru.vladmz.books.DTOs.UserResponse;
import ru.vladmz.books.DTOs.UserUpdateRequest;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.mappers.UserMapper;
import ru.vladmz.books.services.UserService;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService service;

    @Autowired
    public UserController(UserService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserCreateRequest request){
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
    public ResponseEntity<UserResponse> updateUser(@PathVariable Integer id, @RequestBody UserUpdateRequest request){
        UserResponse updated = service.updateUser(request, id);
        return ResponseEntity.status(HttpStatus.OK).body(updated);
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
        service.deleteById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}