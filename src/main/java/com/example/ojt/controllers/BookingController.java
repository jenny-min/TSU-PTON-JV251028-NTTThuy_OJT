package com.example.ojt.controllers;

import com.example.ojt.dtos.booking.BookingResponse;
import com.example.ojt.dtos.movie.MovieResponse;
import com.example.ojt.dtos.showtime.ShowtimeResponse;
import com.example.ojt.entities.Room;
import com.example.ojt.entities.User;
import com.example.ojt.services.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
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
    private  final UserService userService;

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

        Room room = roomService.getRoomById(showtime.getRoomId());


        model.addAttribute("showtime", showtime);
        model.addAttribute("movie", showtime.getMovieId());
        model.addAttribute("room", room);

        List<List<String>> seatRows = new ArrayList<>();


        int rows = room.getSeatsX();
        int cols = room.getSeatsY();

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

    //Xem booking
    @GetMapping("/history")
    public String bookingHistory(Model model,
                                 Principal principal) {

        System.out.println("History");

        Optional<User> user = userService.findByEmail(principal.getName());

        model.addAttribute(
                "histories",
                bookingService.getBookingHistory(user.get().getId()));

        return "user/history";
    }
}
