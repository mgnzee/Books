package ru.vladmz.books.DTOs.user;

import org.hibernate.validator.constraints.URL;

public record UserChangePictureRequest(@URL String pic){}
