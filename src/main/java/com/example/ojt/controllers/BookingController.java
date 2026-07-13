package com.example.ojt.controllers;

import com.example.ojt.dtos.booking.*;
import com.example.ojt.dtos.movie.MovieResponse;
import com.example.ojt.dtos.showtime.ShowtimeResponse;
import com.example.ojt.entities.Room;
import com.example.ojt.entities.User;
import com.example.ojt.services.interfaces.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
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
    public String selectSeat(@PathVariable Long showtimeId, Model model,
                             RedirectAttributes redirectAttributes,
                             HttpSession session) {
        //Hiển thị lỗi nếu đã hết vé
        ShowtimeResponse showtime = showtimeService.getShowtimeById(showtimeId);

        if (showtime.isSoldOut()) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Suất chiếu này đã hết vé."
            );

            return "redirect:/user/movies/" + showtime.getMovieId() + "/showtimes";
        }

        MovieResponse movie = movieService.getMovieById(showtime.getMovieId());

        Room room = roomService.getRoomById(showtime.getRoomId());

        BigDecimal vipPrice = showtime.getTicketPrice().multiply(BigDecimal.valueOf(1.5));
        BigDecimal couplePrice = showtime.getTicketPrice().multiply(BigDecimal.valueOf(2));
        BigDecimal normalPrice = showtime.getTicketPrice();

        model.addAttribute("vipPrice", vipPrice);
        model.addAttribute("couplePrice", couplePrice);
        model.addAttribute("normalPrice", normalPrice);

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

        // XỬ LÝ GHẾ ĐANG CHỌN TRONG SESSION
        Set<String> selectingSeats = new HashSet<>();
        ConfirmBookingRequest currentRequest = (ConfirmBookingRequest) session.getAttribute("BOOKING_REQUEST");

        // Nếu trong session có sẵn request của suất chiếu này (do quay lại)
        if (currentRequest != null && showtimeId.equals(currentRequest.getShowtimeId())) {
            String seatString = currentRequest.getBookingSeat();
            if (seatString != null && !seatString.isBlank()) {
                selectingSeats = Arrays.stream(seatString.split(","))
                        .map(String::trim)
                        .collect(Collectors.toSet());
            }
        }
        // Đẩy danh sách ghế đang chọn ngược lại cho Thymeleaf
        model.addAttribute("selectingSeats", selectingSeats);
        model.addAttribute("bookedSeats", bookingService.getBookedSeats(showtimeId));
        model.addAttribute("vipSeats", vipSeats);
        model.addAttribute("seatRows", seatRows);
        model.addAttribute("coupleSeats", coupleSeats);
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

    //chuyển sang trang Thanh toán
    @PostMapping("/bookings/payment")
    public String payment(@ModelAttribute BookingRequest request,
                          Model model) {

        ConfirmBookingResponse confirm =
                bookingService.buildConfirm(request);

        model.addAttribute("confirm", confirm);

        model.addAttribute("request", request);

        return "user/payment";
    }

    // Hàm Checkout
    @PostMapping("/bookings/checkout")
    public String checkout(@ModelAttribute BookingRequest request,
                           Principal principal,
                           RedirectAttributes redirectAttributes) {
        try {
            TicketResponse ticket = bookingService.checkout(request, principal.getName());
            redirectAttributes.addFlashAttribute("ticket", ticket);
            redirectAttributes.addFlashAttribute("success", "Đặt vé thành công.");

            // Chuyển hướng về link dùng chung
            return "redirect:/bookings/history";

        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute("error", ex.getMessage());
            return "redirect:/bookings/history";
        }
    }

    // Hàm xem Lịch sử
    @GetMapping("/bookings/history")
    public String bookingHistory(Model model,
                                 Principal principal,
                                 HttpServletRequest request) {

        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        List<TicketResponse> histories;

        if (request.isUserInRole("ADMIN") || request.isUserInRole("STAFF")) {
            histories = bookingService.getAllBookings();
        } else {
            histories = bookingService.getBookingHistory(user.getId());
        }

        model.addAttribute("histories", histories);
        return "shared/booking-history";
    }

    //hủy vé
    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            bookingService.cancelBooking(id);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Hủy vé thành công."
            );

        } catch (RuntimeException ex) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    ex.getMessage()
            );
        }
        return "redirect:/bookings/history";
    }
}
