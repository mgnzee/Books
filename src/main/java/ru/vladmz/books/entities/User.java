package ru.vladmz.books.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ru.vladmz.books.entities.interfaces.Ownable;
import ru.vladmz.books.entities.interfaces.SoftDeletable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User extends BaseEntity implements UserDetails, Ownable, SoftDeletable {

    @NotBlank(message="name required")
    @Column(nullable = false)
    @Size(min = 1, max = 25)
    private String name;

    @NotBlank(message = "email required")
    @Email(message = "invalid email")
    @Column(unique = true, nullable = false)
    private String email;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;
    @Column(name = "is_disabled")
    private boolean isDisabled = false;

    @Column(name = "profile_pic_url")
    private String profilePicture;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "uploadedBy")
    private List<Book> books = new ArrayList<>();

    @OneToMany(mappedBy = "author")
    private List<Bookshelf> bookshelves = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();

    public User(){}

    public User(Integer id, String name, String email, String profilePicture) {
        this.setId(id);
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
    }

    @JsonIgnore
    @Override
    public User getOwner() {
        return this;
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

    public void addBookshelf(Bookshelf bookshelf){
        this.bookshelves.add(bookshelf);
        bookshelf.setAuthor(this);
    }

    public void addPost(Post post){
        this.posts.add(post);
        post.setUser(this);
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public Boolean isDeleted() {
        return isDeleted;
    }

    public void setDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    public Boolean isDisabled() {
        return isDisabled;
    }

    public void setDisabled(boolean disabled) {
        isDisabled = disabled;
    }

    public String getPassword() {
        return password;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isDeleted;
    }

    @Override
    public boolean isEnabled() {
        return !isDisabled;
    }
}
