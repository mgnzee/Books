package ru.vladmz.books.targetStrategies;

import org.springframework.stereotype.Component;
import ru.vladmz.books.entities.interfaces.Commentable;
import ru.vladmz.books.etc.TargetType;
import ru.vladmz.books.exceptions.BookNotFoundException;
import ru.vladmz.books.repositories.BookRepository;

@Component
public class BookTargetStrategy implements CommentTargetStrategy {

    private final BookRepository repository;

    public BookTargetStrategy(BookRepository repository) {
        this.repository = repository;
    }

    @Override
    public TargetType getType() {
        return TargetType.BOOK;
    }

    @Override
    public Commentable findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new BookNotFoundException(id));
    }
}
