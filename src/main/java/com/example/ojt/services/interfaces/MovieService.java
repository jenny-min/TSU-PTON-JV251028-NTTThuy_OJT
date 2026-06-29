package com.example.ojt.services.interfaces;

import com.example.ojt.dtos.movie.CreateMovieRequest;
import com.example.ojt.dtos.movie.MovieResponse;
import com.example.ojt.dtos.movie.UpdateMovieRequest;
import com.example.ojt.entities.Movie;
import org.springframework.data.domain.Page;
import java.util.List;

public interface MovieService {
    List<Movie> getAllMovies();

    Page<MovieResponse> getMovies(int page, int size);

    MovieResponse getMovieById(Long id);

    Movie addMovie(CreateMovieRequest request);

    UpdateMovieRequest getMovieForEdit(Long id);

    Movie editMovie(Long id, UpdateMovieRequest request);

    void deleteMovie(Long id);

    MovieResponse getById(Long movieId);
}
