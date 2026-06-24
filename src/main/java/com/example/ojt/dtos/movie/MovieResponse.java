package com.example.ojt.dtos.movie;

import com.example.ojt.roles.MovieStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResponse {
    private Long movieId;
    private String title;
    private String description;
    private int duration;
    private LocalDate releaseDate;
    private String language;
    private String posterUrl;
    private String trailerUrl;
    private String ageRating;
    private MovieStatus status;
    private LocalDateTime createdAt;
    private Set<String> genres;
}
