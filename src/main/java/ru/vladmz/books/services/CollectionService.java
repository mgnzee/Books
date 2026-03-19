package ru.vladmz.books.services;

import org.springframework.stereotype.Service;
import ru.vladmz.books.DTOs.BookResponse;
import ru.vladmz.books.DTOs.CollectionResponse;
import ru.vladmz.books.entities.Collection;
import ru.vladmz.books.repositories.CollectionRepository;

import java.util.List;

@Service
public class CollectionService {

    //TODO: ADD OTHER CRUD METHODS

    private final CollectionRepository repository;

    public CollectionService(CollectionRepository repository) {
        this.repository = repository;
    }

    public List<CollectionResponse> findAll(){
        return repository.findAll().stream().map(CollectionResponse::new).toList();
    }

    public CollectionResponse findById(Integer id){
        return new CollectionResponse(repository.findById(id).orElseThrow(() -> new RuntimeException("Collection with id: + " + id + " not found")));
    }

    public CollectionResponse createCollection(Collection collection) {
        return new CollectionResponse(repository.save(collection));
    }

    public List<BookResponse> findBooksByCollectionId(Integer id) {
        Collection collection = repository.findById(id).orElseThrow(() -> new RuntimeException("Collection with id: + " + id + " not found"));
        return collection.getBooks().stream().map(BookResponse::new).toList();
    }

    public CollectionResponse updateCollection(Integer id, Collection collection){
        Collection currentCollection = repository.findById(id).orElseThrow(() -> new RuntimeException("Collection with id: + " + id + " not found"));
        if(collection.getTitle() != null) currentCollection.setTitle(collection.getTitle());
        if(collection.getDescription() != null) currentCollection.setDescription(collection.getDescription());
        if(collection.getCover() != null) currentCollection.setCover(collection.getCover());
        return new CollectionResponse(repository.save(currentCollection));
    }

    public void deleteCollection(Integer id){
        repository.deleteById(id);
    }
}
