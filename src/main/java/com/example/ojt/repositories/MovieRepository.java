package com.example.ojt.repositories;

import com.example.ojt.entities.Movie;
import com.example.ojt.enums.MovieStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    // Đếm số lượng phim theo trạng thái (Ví dụ: ON_SCREEN)
    long countByStatus(MovieStatus status);
}
