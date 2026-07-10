package com.example.ojt.services.imps;

import com.example.ojt.dtos.movie.CreateMovieRequest;
import com.example.ojt.dtos.movie.MovieResponse;
import com.example.ojt.dtos.movie.UpdateMovieRequest;
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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final GenreRepository genreRepository;

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Page<MovieResponse> getMovies(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return movieRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    public MovieResponse getMovieById(Long id) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy phim"));

        return toResponse(movie);
    }

    @Override
    public Movie addMovie(CreateMovieRequest request) {

        Movie movie = new Movie();

        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setDuration(request.getDuration());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setLanguage(request.getLanguage());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setTrailerUrl(request.getTrailerUrl());
        movie.setAgeRating(request.getAgeRating());
        movie.setStatus(request.getStatus());
        movie.setCreatedAt(LocalDateTime.now());

        Set<Genre> genres =
                new HashSet<>(
                        genreRepository.findAllById(
                                request.getGenreIds()
                        ));

        movie.setGenres(genres);

        return movieRepository.save(movie);
    }

    @Override
    public UpdateMovieRequest getMovieForEdit(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy phim"));

        return UpdateMovieRequest.builder()
                .movieId(movie.getMovieId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .duration(movie.getDuration())
                .releaseDate(movie.getReleaseDate())
                .language(movie.getLanguage())
                .posterUrl(movie.getPosterUrl())
                .trailerUrl(movie.getTrailerUrl())
                .ageRating(movie.getAgeRating())
                .status(movie.getStatus())
                .genreIds(movie.getGenres()
                                .stream()
                                .map(Genre::getGenreId)
                                .collect(Collectors.toSet())
                )
                .build();
    }

    @Override
    public MovieResponse editMovie(
            Long id,
            UpdateMovieRequest request
    ) {

        Movie movie = movieRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy phim"));

        movie.setTitle(request.getTitle());
        movie.setDescription(request.getDescription());
        movie.setDuration(request.getDuration());
        movie.setReleaseDate(request.getReleaseDate());
        movie.setLanguage(request.getLanguage());
        movie.setPosterUrl(request.getPosterUrl());
        movie.setTrailerUrl(request.getTrailerUrl());
        movie.setAgeRating(request.getAgeRating());
        movie.setStatus(request.getStatus());

        Set<Genre> genres =
                new HashSet<>(
                        genreRepository.findAllById(
                                request.getGenreIds()
                        ));

        movie.setGenres(genres);

        System.out.println("genre size" + genres.size());

        return toResponse(movieRepository.save(movie));
    }

    @Override
    public void deleteMovie(Long id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));

        movie.getGenres().clear();

        movieRepository.delete(movie);
    }

    @Override
    public List<Movie> getActiveMovies() {
        return movieRepository.findByStatus("ACTIVE");
    }

    @Override
    public MovieResponse getById(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy phim"));

        return toResponse(movie);
    }

    private MovieResponse toResponse(Movie movie) {
        return MovieResponse.builder()
                .movieId(movie.getMovieId())
                .title(movie.getTitle())
                .description(movie.getDescription())
                .duration(movie.getDuration())
                .releaseDate(movie.getReleaseDate())
                .language(movie.getLanguage())
                .posterUrl(movie.getPosterUrl())
                .trailerUrl(movie.getTrailerUrl())
                .ageRating(movie.getAgeRating())
                .status(movie.getStatus())
                .createdAt(movie.getCreatedAt())
                .genres( movie.getGenres()
                        .stream()
                        .map(Genre::getGenreName)
                        .collect(Collectors.toSet()) )
                .build();
    }
}
