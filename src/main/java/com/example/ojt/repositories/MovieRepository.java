package com.example.ojt.repositories;

import com.example.ojt.entities.Movie;
import com.example.ojt.enums.MovieStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    long countByStatus(MovieStatus status);
    List<Movie> findByStatus(String status);
}
