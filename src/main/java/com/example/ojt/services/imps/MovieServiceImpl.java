package com.example.ojt.services.imps;

import com.example.ojt.entities.Genre;
import com.example.ojt.entities.Movie;
import com.example.ojt.repositories.GenreRepository;
import com.example.ojt.repositories.MovieRepository;
import com.example.ojt.services.interfaces.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    @Override
    public Page<Movie> getMovies(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return movieRepository.findAll(pageable);
    }

    @Override
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));
    }

    @Override
    public Movie addMovie(Movie movie, List<Long> genreIds) {

        Set<Genre> genres = new HashSet<>(
                genreRepository.findAllById(genreIds)
        );

        movie.setGenres(genres);

        movie.setCreatedAt(LocalDateTime.now());

        return movieRepository.save(movie);
    }

    @Override
    public Movie editMovie(Long id, Movie movie, List<Long> genreIds) {
        Movie existingMovie = getMovieById(id);

        existingMovie.setTitle(movie.getTitle());
        existingMovie.setDescription(movie.getDescription());
        existingMovie.setDuration(movie.getDuration());
        existingMovie.setReleaseDate(movie.getReleaseDate());
        existingMovie.setLanguage(movie.getLanguage());
        existingMovie.setPosterUrl(movie.getPosterUrl());
        existingMovie.setTrailerUrl(movie.getTrailerUrl());
        existingMovie.setAgeRating(movie.getAgeRating());
        existingMovie.setStatus(movie.getStatus());

        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(genreIds));

        existingMovie.setGenres(genres);

        return movieRepository.save(existingMovie);
    }

    @Override
    public void deleteMovie(Long id) {
        System.out.println("SERVICE DELETE: " + id);

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));

        System.out.println("Showtimes = " + movie.getShowtimes().size());

        movie.getGenres().clear();

        movieRepository.delete(movie);

        System.out.println("DELETE DONE");
    }
}
