package com.example.ojt.controllers;

import com.example.ojt.dtos.booking.BookingResponse;
import com.example.ojt.dtos.movie.MovieResponse;
import com.example.ojt.dtos.showtime.ShowtimeResponse;
import com.example.ojt.entities.Movie;
import com.example.ojt.services.interfaces.BookingService;
import com.example.ojt.services.interfaces.MovieService;
import com.example.ojt.services.interfaces.RoomService;
import com.example.ojt.services.interfaces.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class BookingController {
    private final ShowtimeService showtimeService;
    private final MovieService movieService;
    private final RoomService roomService;
    private final BookingService bookingService;

    @GetMapping("/movies/{movieId}/showtimes")
    public String showShowtimes(@PathVariable Long movieId,
                                Model model) {

        MovieResponse movie = movieService.getMovieById(movieId);

        if (movie == null) {
            throw new RuntimeException("Movie not found: " + movieId);
        }

        List<ShowtimeResponse> showtimes =
                Optional.ofNullable(showtimeService.getByMovieId(movieId))
                        .orElse(Collections.emptyList());

        model.addAttribute("movie", movie);
        model.addAttribute("showtimes", showtimes);

        return "user/showtime-list";
    }

    @GetMapping("/bookings/showtime/{showtimeId}")
    public String selectSeat(@PathVariable Long showtimeId, Model model) {

        ShowtimeResponse showtime = showtimeService.getShowtimeById(showtimeId);

        model.addAttribute("showtime", showtime);
        model.addAttribute("movie", showtime.getMovie());
        model.addAttribute("room", showtime.getRoom());

        List<List<String>> seatRows = new ArrayList<>();

        int rows = showtime.getRoom().getSeatsX();
        int cols = showtime.getRoom().getSeatsY();

        for (int i = 0; i < rows; i++) {
            List<String> row = new ArrayList<>();

            char rowName = (char) ('A' + i);

            for (int j = 1; j <= cols; j++) {
                row.add(rowName + String.valueOf(j));
            }

            seatRows.add(row);
        }

        model.addAttribute("seatRows", seatRows);

        return "user/select-seat";
    }

    @GetMapping("/user/bookings")
    public String myBookings(Authentication auth, Model model) {

        List<BookingResponse> bookings =
                bookingService.getMyBookings(auth.getName());

        model.addAttribute("bookings", bookings);

        return "user/my-bookings";
    }
}
