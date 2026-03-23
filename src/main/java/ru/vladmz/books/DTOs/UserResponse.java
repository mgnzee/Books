package ru.vladmz.books.DTOs;

import ru.vladmz.books.entities.User;

public class UserResponse {

    private Integer id;
    private String name;
    private String email;
    private String profilePicture;

    public UserResponse(Integer id, String name, String email, String profilePicture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.profilePicture = profilePicture;
    }

//    public UserResponse(User user){
//        this.id = user.getId();
//        this.name = user.getName();
//        this.email = user.getEmail();
//        this.profilePicture = user.getProfilePicture();
//    }

    public Integer getId() {
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
}
