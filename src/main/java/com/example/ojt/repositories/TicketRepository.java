package com.example.ojt.repositories;

import com.example.ojt.entities.Booking;
import com.example.ojt.entities.Showtime;
import com.example.ojt.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket,Long> {
    boolean existsByShowtimeAndSeatCode(Showtime showtime, String seatCode);

    List<Ticket> findByBooking(Booking booking);
}
