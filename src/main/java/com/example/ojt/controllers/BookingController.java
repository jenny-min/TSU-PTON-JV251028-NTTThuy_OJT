package com.example.ojt.controllers;

import com.example.ojt.dtos.booking.BookingRequest;
import com.example.ojt.dtos.booking.BookingResponse;
import com.example.ojt.dtos.booking.ConfirmBookingRequest;
import com.example.ojt.dtos.booking.ConfirmBookingResponse;
import com.example.ojt.dtos.movie.MovieResponse;
import com.example.ojt.dtos.showtime.ShowtimeResponse;
import com.example.ojt.entities.Room;
import com.example.ojt.entities.User;
import com.example.ojt.services.interfaces.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

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

    //Chọn ghế
    @GetMapping("/bookings/showtime/{showtimeId}")
    public String selectSeat(@PathVariable Long showtimeId, Model model) {

        ShowtimeResponse showtime = showtimeService.getShowtimeById(showtimeId);

        MovieResponse movie = movieService.getMovieById(showtime.getMovieId());

        Room room = roomService.getRoomById(showtime.getRoomId());

        model.addAttribute("showtime", showtime);
        model.addAttribute("movie", movie);
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

        Set<String> vipSeats = new HashSet<>();

        if (room.getVipSeats() != null && !room.getVipSeats().isBlank()) {
            vipSeats = Arrays.stream(room.getVipSeats().split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());
        }

        Set<String> coupleSeats = new HashSet<>();

        if (room.getCoupleSeats() != null && !room.getCoupleSeats().isBlank()) {
            coupleSeats = Arrays.stream(room.getCoupleSeats().split(","))
                    .map(String::trim)
                    .collect(Collectors.toSet());
        }

        model.addAttribute("coupleSeats", coupleSeats);
        model.addAttribute("vipSeats", vipSeats);
        model.addAttribute("seatRows", seatRows);
        model.addAttribute("bookedSeats", new HashSet<String>());
        model.addAttribute("ticketPrice", showtime.getTicketPrice());

        return "user/select-seat";
    }

    //Lưu request
    @PostMapping("/bookings/confirm")
    public String confirm(@ModelAttribute ConfirmBookingRequest request,
                          HttpSession session) {

        if (request.getBookingSeat() == null || request.getBookingSeat().isBlank()) {
            return "redirect:/user/showtimes";
        }

        session.setAttribute("BOOKING_REQUEST", request);

        return "redirect:/user/bookings/confirm/view";
    }

    //page confirm
    @GetMapping("/bookings/confirm/view")
    public String confirmView(HttpSession session,
                              Model model) {

        ConfirmBookingRequest request =
                (ConfirmBookingRequest) session.getAttribute("BOOKING_REQUEST");

        if (request == null || request.getShowtimeId() == null) {
            return "redirect:/user/showtimes";
        }

        ConfirmBookingResponse response =
                bookingService.getConfirmBooking(request);

        model.addAttribute("confirm", response);

        return "user/confirm-booking";
    }

    //Thanh toán
    @PostMapping("/bookings/payment")
    public String payment(@ModelAttribute BookingRequest request,
                          Model model) {

        model.addAttribute("request", request);

        return "user/payment";
    }

    //Xem lịch sử booking
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
