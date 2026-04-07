package ru.vladmz.books.targetStrategies;

import org.springframework.stereotype.Component;
import ru.vladmz.books.entities.interfaces.Commentable;
import ru.vladmz.books.etc.TargetType;
import ru.vladmz.books.exceptions.PostNotFoundException;
import ru.vladmz.books.repositories.PostDao;

@Component
public class PostTargetStrategy implements CommentTargetStrategy{

    private final PostDao postDao;

    public PostTargetStrategy(PostDao postDao) {
        this.postDao = postDao;
    }

    @Override
    public TargetType getType() {
        return TargetType.POST;
    }

    @Override
    public Commentable findById(Integer id) {
        return postDao.findById(id).orElseThrow(() -> new PostNotFoundException(id));
    }
}
