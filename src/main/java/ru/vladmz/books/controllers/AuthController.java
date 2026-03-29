package ru.vladmz.books.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.vladmz.books.DTOs.JwtResponse;
import ru.vladmz.books.DTOs.LoginRequest;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.security.JwtUtil;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager manager;

    private final JwtUtil jwtUtil;

    @Autowired
    public AuthController(AuthenticationManager manager, JwtUtil jwtUtil) {
        this.manager = manager;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/test")
    public String test(){
        return "ok";
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid LoginRequest request){
        Authentication authentication = manager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );
        User user = (User) authentication.getPrincipal();
        String token = jwtUtil.generateToken(user);
        return ResponseEntity.ok(new JwtResponse(token));
    }
}
