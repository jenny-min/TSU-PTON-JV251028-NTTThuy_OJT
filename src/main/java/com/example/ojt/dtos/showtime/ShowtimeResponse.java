package com.example.ojt.dtos.showtime;

import com.example.ojt.dtos.movie.MovieResponse;
import com.example.ojt.entities.Booking;
import com.example.ojt.entities.Movie;
import com.example.ojt.entities.Room;
import com.example.ojt.roles.ShowtimeStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShowtimeResponse {
    private Long movieId;
    private String movieTitle;
    private Long roomId;
    private String roomName;
    private Long showtimeId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BigDecimal ticketPrice;
    private ShowtimeStatus status;
    private List<Booking> bookings;
    private MovieResponse movie;
}
