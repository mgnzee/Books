package ru.vladmz.books.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message="name required")
    @Column(nullable = false)
    @Size(min = 1, max = 25)
    private String name;

    @NotBlank(message = "email required")
    @Email(message = "invalid email")
    @Column(unique = true, nullable = false)
    private String email;

    @URL
    @Column(name = "profile_pic_url")
    private String profilePicture;

    @OneToMany(mappedBy = "uploadedBy")
    private List<Book> books = new ArrayList<>();

    public User(){}

    public User(Integer id, String name, String email, String profilePicture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    public int getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public void addBook(Book book){
        this.books.add(book);
        book.setUploadedBy(this);
    }
}
