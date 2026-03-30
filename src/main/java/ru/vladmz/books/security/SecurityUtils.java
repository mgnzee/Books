package ru.vladmz.books.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.vladmz.books.exceptions.UserNotAuthenticatedException;

@Component
public class SecurityUtils {

    public String getCurrentUserEmail(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated()) throw new UserNotAuthenticatedException("Not authenticated");
        return auth.getName();
    }
}
