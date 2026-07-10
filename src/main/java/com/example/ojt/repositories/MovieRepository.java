package com.example.ojt.repositories;

import com.example.ojt.entities.Movie;
import com.example.ojt.enums.MovieStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    long countByStatus(MovieStatus status);

    List<Movie> findByStatus(MovieStatus status);

    @Query("SELECT DISTINCT m FROM Movie m " +
            "JOIN m.showtimes st " +
            "WHERE m.status = :movieStatus " +
            "AND st.status = com.example.ojt.enums.ShowtimeStatus.PUBLISHED " +
            "AND st.startTime > :now")
    List<Movie> findActiveMoviesWithAvailableShowtimes(
            @Param("movieStatus") MovieStatus movieStatus,
            @Param("now") LocalDateTime now
    );
}
