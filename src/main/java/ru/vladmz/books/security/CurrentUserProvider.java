package ru.vladmz.books.security;

import org.springframework.stereotype.Component;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.exceptions.UserNotFoundException;
import ru.vladmz.books.repositories.UserRepository;

@Component
public class CurrentUserProvider {

    private final UserRepository userRepository;
    private final SecurityUtils securityUtils;

    public CurrentUserProvider(UserRepository userRepository, SecurityUtils securityUtils) {
        this.userRepository = userRepository;
        this.securityUtils = securityUtils;
    }

    public User get(){
        String userEmail = securityUtils.getCurrentUserEmail();
        return userRepository.findByEmail(userEmail).orElseThrow(() -> new UserNotFoundException(userEmail));
    }

    public String getEmail(){
        return securityUtils.getCurrentUserEmail();
    }
}
