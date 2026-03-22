package ru.vladmz.books.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.vladmz.books.DTOs.BookshelfResponse;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.services.UserService;

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
    public ResponseEntity<User> createUser(@RequestBody @Valid User user){
        User created = service.createUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public List<User> selectAll(){
        return service.findAll();
    }

    @GetMapping("/{id}")
    public User selectById(@PathVariable Integer id){
        return service.findById(id);
    }

    @GetMapping("/{id}/bookshelves")
    public List<BookshelfResponse> selectBookshelves(@PathVariable Integer id){
        return service.findBookshelvesOfUser(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Integer id, @RequestBody User user){
        User updated = service.updateUser(user, id);
        return ResponseEntity.status(HttpStatus.CREATED).body(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id){
        service.deleteUser(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}