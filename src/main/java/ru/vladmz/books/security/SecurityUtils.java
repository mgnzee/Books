package ru.vladmz.books.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.exceptions.UserNotAuthenticatedException;
import ru.vladmz.books.services.UserService;

@Component
public class SecurityUtils {

    private static UserService service;

    @Autowired
    public void setService(UserService service){
        SecurityUtils.service = service;
    }

    public static User getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated()) throw new UserNotAuthenticatedException("Not authenticated");
        String email = auth.getName();
        return service.findByEmail(email);
    }
}
