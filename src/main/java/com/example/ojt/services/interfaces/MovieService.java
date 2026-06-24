package com.example.ojt.services.interfaces;

import com.example.ojt.entities.Movie;
import org.springframework.data.domain.Page;
import java.util.List;

public interface MovieService {
    Page<Movie> getMovies(int page, int size);

    Movie getMovieById(Long id);

    Movie addMovie(Movie movie, List<Long> genreIds);

    Movie editMovie(Long id, Movie movie, List<Long> genreIds);

    void deleteMovie(Long id);
}
