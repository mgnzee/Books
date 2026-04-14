package ru.vladmz.books.services;

import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.vladmz.books.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

//        return org.springframework.security.core.userdetails.User
//                .withUsername(user.getEmail())
//                .password(user.getPassword())
//                .authorities("USER")
//                .accountLocked(user.isDisabled())
//                .disabled(user.isDeleted())
//                .build();
    }

    public UserDetails loadUserById(Integer userId) throws UsernameNotFoundException {
        return userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

//        return org.springframework.security.core.userdetails.User
//                .withUsername(user.getEmail())
//                .password(user.getPassword())
//                .authorities("USER")
//                .accountLocked(user.isDisabled())
//                .disabled(user.isDeleted())
//                .build();
    }
}
