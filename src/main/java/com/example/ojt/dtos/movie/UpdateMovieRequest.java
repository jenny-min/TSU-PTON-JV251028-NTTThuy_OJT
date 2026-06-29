package com.example.ojt.dtos.movie;

import com.example.ojt.roles.MovieStatus;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateMovieRequest {
    @NotNull
    private Long movieId;

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @Min(1)
    @Max(500)
    private int duration;

    @NotNull
    private LocalDate releaseDate;

    @NotBlank
    private String language;

    private String posterUrl;
    private String trailerUrl;

    @NotBlank
    private String ageRating;

    @NotNull
    private MovieStatus status;
    private Set<Long> genreIds;
}
