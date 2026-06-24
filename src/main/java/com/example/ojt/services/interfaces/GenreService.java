package com.example.ojt.services.interfaces;

import com.example.ojt.entities.Genre;

import java.util.List;

public interface GenreService {
    List<Genre> findAll();

    Genre findById(Long id);

    Genre save(Genre genre);

    void delete(Long id);
}
