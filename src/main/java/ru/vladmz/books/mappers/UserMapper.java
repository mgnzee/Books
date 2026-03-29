package ru.vladmz.books.mappers;

import ru.vladmz.books.DTOs.user.UserCreateRequest;
import ru.vladmz.books.DTOs.user.UserResponse;
import ru.vladmz.books.DTOs.user.UserPatchRequest;
import ru.vladmz.books.entities.User;

public class UserMapper {

    //to prevent instantiation
    private UserMapper(){}

    public static User toUser(UserCreateRequest request){
        User user = new User();
        user.setName(request.getName());
        user.setEmail(request.getEmail());
        user.setProfilePicture(request.getProfilePicture());

        return user;
    }


    public static User patchUser(User user, UserPatchRequest request){
        if(request.getName() != null) user.setName(request.getName());
        //if(request.getEmail() != null) user.setEmail(request.getEmail());
        if(request.getProfilePicture() != null) user.setProfilePicture(request.getProfilePicture());

        return user;
    }

    public static UserResponse toResponse(User user){
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getProfilePicture()
        );
    }
}
