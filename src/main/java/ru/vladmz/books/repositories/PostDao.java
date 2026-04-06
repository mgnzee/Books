package ru.vladmz.books.repositories;

import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.vladmz.books.entities.Post;

import java.util.List;

@Component
public class PostDao extends AbstractDao<Post, Integer>{

    @Autowired
    public PostDao(EntityManager em) {
        super(em, Post.class);
    }

    @Override
    public List<Post> findAll() {
        return em.createQuery("select p from Post p join fetch p.user", Post.class).getResultList();
    }
}
