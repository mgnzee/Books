package ru.vladmz.books.targetStrategies;

import org.springframework.stereotype.Component;
import ru.vladmz.books.entities.interfaces.Commentable;
import ru.vladmz.books.etc.TargetType;
import ru.vladmz.books.exceptions.BookshelfNotFoundException;
import ru.vladmz.books.repositories.BookshelfRepository;

@Component
public class BookshelfTargetStrategy implements CommentTargetStrategy{

    private final BookshelfRepository repository;

    public BookshelfTargetStrategy(BookshelfRepository repository) {
        this.repository = repository;
    }

    @Override
    public TargetType getType() {
        return TargetType.BOOKSHELF;
    }

    @Override
    public Commentable findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new BookshelfNotFoundException(id));
    }
}
