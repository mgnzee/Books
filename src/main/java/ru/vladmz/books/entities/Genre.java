package ru.vladmz.books.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "genres")
public class Genre extends BaseEntity{

    @Column(updatable = false, unique = true, nullable = false)
    private String title;

    public Genre(){}

    /**
    * ONLY FOR TESTS
    **/
    public Genre(Integer id, String title){
        this.setId(id);
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre genre)) return false;
        return getId() != null && getId().equals(genre.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(title);
    }
}
