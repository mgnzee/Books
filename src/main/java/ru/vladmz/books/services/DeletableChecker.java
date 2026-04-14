package ru.vladmz.books.services;

import org.jspecify.annotations.NonNull;
import ru.vladmz.books.entities.interfaces.SoftDeletable;
import ru.vladmz.books.exceptions.ResourceAlreadyDeletedException;

public interface DeletableChecker {
    default void checkDeleted(@NonNull SoftDeletable entity){
        if (entity.isDeleted()) throw new ResourceAlreadyDeletedException(entity.getId());
    }
}
