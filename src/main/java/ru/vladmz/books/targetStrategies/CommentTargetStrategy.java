package ru.vladmz.books.targetStrategies;

import ru.vladmz.books.entities.interfaces.Commentable;
import ru.vladmz.books.etc.TargetType;

public interface CommentTargetStrategy {
    TargetType getType();
    Commentable findById(Integer id);
}
