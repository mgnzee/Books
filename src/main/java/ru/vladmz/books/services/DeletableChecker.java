package ru.vladmz.books.services;

import org.jspecify.annotations.NonNull;
import ru.vladmz.books.entities.interfaces.SoftDeletable;
import ru.vladmz.books.exceptions.ResourceAlreadyDeleted;

public interface DeletableChecker {
    default void checkDeleted(@NonNull SoftDeletable entity){
        if (entity.isDeleted()) throw new ResourceAlreadyDeleted(entity.getId());
    }
}
