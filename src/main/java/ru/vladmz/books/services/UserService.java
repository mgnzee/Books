package ru.vladmz.books.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.vladmz.books.DTOs.BookshelfResponse;
import ru.vladmz.books.entities.Bookshelf;
import ru.vladmz.books.entities.User;
import ru.vladmz.books.repositories.BookshelfRepository;
import ru.vladmz.books.repositories.UserRepository;

import java.util.List;

@Service
public class UserService {

    private final UserRepository repository;
    private final BookshelfRepository bookshelfRepository;

    @Autowired
    public UserService(UserRepository repository, BookshelfRepository bookshelfRepository) {
        this.repository = repository;
        this.bookshelfRepository = bookshelfRepository;
    }

    public List<User> findAll(){
        return repository.findAll();
    }

    public User findById(Integer id){
        return repository.findById(id).orElseThrow(() -> new RuntimeException("User with id: " + id + " not found."));
    }

    public List<BookshelfResponse> findBookshelvesOfUser(Integer id){
        return bookshelfRepository.findByAuthorId(id).stream().map(BookshelfResponse::new).toList();
    }

    @Transactional
    public User createUser(User user){
        User newUser = repository.save(user);
        Bookshelf newBookshelf = new Bookshelf();
        newBookshelf.setTitle("My Library");
        newBookshelf.setDescription("This is default bookshelf created automatically");
        newBookshelf.setAuthor(newUser);
        bookshelfRepository.save(newBookshelf);
        return newUser;
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
