package com.example.ojt.entities;

import com.example.ojt.enums.MovieStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "movies")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long movieId;

    @NotBlank(message = "Tên phim không được để trống")
    @Size(max = 255, message = "Tên phim không được vượt quá 255 ký tự")
    @Column(nullable = false, length = 255)
    private String title;

    @NotBlank(message = "Mô tả phim không được để trống")
    @Column(columnDefinition = "TEXT")
    private String description;

    @Min(value = 1, message = "Thời lượng phim phải lớn hơn 0 phút")
    @Max(value = 500, message = "Thời lượng phim không hợp lệ")
    private int duration;

    @NotNull(message = "Ngày phát hành không được để trống")
    private LocalDate releaseDate;

    @NotBlank(message = "Ngôn ngữ không được để trống")
    @Size(max = 50, message = "Ngôn ngữ không được vượt quá 50 ký tự")
    private String language;

    @Size(max = 255, message = "URL poster không được vượt quá 255 ký tự")
    private String posterUrl;

    @Size(max = 255, message = "URL trailer không được vượt quá 255 ký tự")
    private String trailerUrl;

    @NotBlank(message = "Độ tuổi giới hạn không được để trống")
    @Size(max = 20, message = "Độ tuổi giới hạn không được vượt quá 20 ký tự")
    private String ageRating;

    @NotNull(message = "Trạng thái phim không được để trống")
    @Enumerated(EnumType.STRING)
    private MovieStatus status;

    private LocalDateTime createdAt;

    @ManyToMany
    @JoinTable(
            name = "movie_genres",
            joinColumns = @JoinColumn(name = "movie_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private Set<Genre> genres;

    @OneToMany(mappedBy = "movie")
    private List<Showtime> showtimes;
}