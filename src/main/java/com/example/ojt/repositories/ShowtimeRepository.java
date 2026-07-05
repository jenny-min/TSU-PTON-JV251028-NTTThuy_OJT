package com.example.ojt.repositories;

import com.example.ojt.entities.Showtime;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByRoomRoomId(Long roomId);

    List<Showtime> findByMovieMovieId(Long movieId);

    @Query("""
    SELECT COUNT(s)
    FROM Showtime s
    WHERE s.room.roomId = :roomId
    AND (:showtimeId IS NULL OR s.showtimeId <> :showtimeId)
    AND :startTime < s.endTime
    AND :endTime > s.startTime
""")
    long countConflicts(Long roomId, Long showtimeId, LocalDateTime startTime, LocalDateTime endTime);
}
