package com.example.ojt.repositories;

import com.example.ojt.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser_Username(String username);

    //Query join booking->showtime->movie->room->ticket
    @Query("""
        SELECT DISTINCT b
        FROM Booking b
        JOIN FETCH b.showtime s
        JOIN FETCH s.movie
        JOIN FETCH s.room
        LEFT JOIN FETCH b.tickets
        WHERE b.user.id = :userId
        ORDER BY b.bookingDate DESC
    """)
    List<Booking> findHistoryByUser(Long userId);
}
