package ru.vladmz.books.entities;

import jakarta.persistence.*;

import java.util.Objects;

@Entity
@Table(name = "genres")
public class Genre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(updatable = false, unique = true, nullable = false)
    private String title;

//    @ManyToMany(mappedBy = "genres")
//    private Set<Book> books = new HashSet<>();

    public Genre(){}


    public Integer getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

//    public Set<Book> getBooks() {
//        return books;
//    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genre genre)) return false;
        return id != null && id.equals(genre.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
