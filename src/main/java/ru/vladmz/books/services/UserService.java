package ru.vladmz.books.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.repositories.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;

    @Autowired
    public UserService(UserRepository repository) {
        this.repository = repository;
    }

    public List<User> findAll(){
        return repository.findAll();
    }

    public User findById(Integer id){
        return repository.findById(id).orElseThrow(() -> new RuntimeException("User with id: " + id + " not found."));
    }

    public User createUser(User user){
        return repository.save(user);
    }

    public User updateUser(User user, Integer id){
        User currentUser = repository.findById(id).orElseThrow(() -> new RuntimeException("User with id: " + id + " not found."));
        if(user.getName() != null) currentUser.setName(user.getName());
        if(user.getEmail() != null) currentUser.setEmail(user.getEmail());
        if(user.getProfilePicture() != null) currentUser.setProfilePicture(user.getProfilePicture());
        return repository.save(currentUser);
    }

    public void deleteUser(Integer id){
        repository.deleteById(id);
    }
}
