package ru.vladmz.books.repositories;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

public abstract class AbstractDao<T, ID> implements BaseDao<T, ID> {

    protected final EntityManager em;
    private final Class<T> entityClass;

    protected AbstractDao(EntityManager em, Class<T> entityClass){
        this.em = em;
        this.entityClass = entityClass;
    }

    @Override
    public Optional<T> findById(ID id) {
        return Optional.ofNullable(em.find(entityClass, id));
    }

    @Override
    public List<T> findAll() {
        return em.createQuery("select e from " + entityClass.getName() + " e", entityClass).getResultList();
    }

    @Override
    public void save(T entity) {
        em.persist(entity);
    }

    @Override
    public T update(T entity) {
        return em.merge(entity);
    }

    @Override
    public void delete(T entity) {
        if(em.contains(entity)) em.remove(entity);
        else em.remove(em.merge(entity));
    }

    @Override
    public boolean deleteById(ID id) {
        T entity = em.find(entityClass, id);
        if(entity != null){
            em.remove(entity);
            return true;
        }
        return false;
    }
}
