package com.example.ojt.controllers;

import com.example.ojt.dtos.booking.TicketResponse;
import com.example.ojt.dtos.showtime.ShowtimeResponse;
import com.example.ojt.entities.User;
import com.example.ojt.services.interfaces.BookingService;
import com.example.ojt.services.interfaces.ShowtimeService;
import com.example.ojt.services.interfaces.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/staff")
@RequiredArgsConstructor
public class StaffController {
    private final UserService userService;
    private final BookingService bookingService;
    private final ShowtimeService showtimeService;

    @GetMapping("/dashboard")
    public String staffDashboard(Model model, Principal principal) {
        if (principal != null) {
            User staff = userService.findByUsername(principal.getName())
                    .orElse(null);
            model.addAttribute("staff", staff);
        }
        return "staff/dashboard";
    }

    @GetMapping("/schedules")
    public String staffSchedules(Model model) {
        try {
            List<ShowtimeResponse> showtimes = showtimeService.getAllShowtimes();

            model.addAttribute("showtimes", showtimes);

        } catch (Exception e) {
            model.addAttribute("error", "Không thể tải danh sách lịch chiếu: " + e.getMessage());
        }

        return "staff/schedules";
    }

    @GetMapping("/bookings/history")
    public String bookingHistory(@RequestParam(value = "bookingId", required = false) Long bookingId,
                                 Model model,
                                 Principal principal,
                                 HttpServletRequest request) {

        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        List<TicketResponse> histories = new ArrayList<>();

        try {
            // TÌM KIẾM NHANH THEO MÃ VÉ
            if (bookingId != null) {
                TicketResponse ticket = bookingService.getBookingById(bookingId);

                // Nếu là USER thường đi tìm kiếm, chỉ cho phép xem vé của CHÍNH HỌ
                if (request.isUserInRole("USER") && !ticket.getUser().getId().equals(user.getId())) {
                    model.addAttribute("error", "Bạn không có quyền xem hóa đơn của người khác.");
                } else {
                    histories.add(ticket);
                }
            }
            // LUỒNG XEM TẤT CẢ MẶC ĐỊNH
            else {
                if (request.isUserInRole("ADMIN") || request.isUserInRole("STAFF")) {
                    histories = bookingService.getAllBookings();
                } else {
                    histories = bookingService.getBookingHistory(user.getId());
                }
            }
        } catch (RuntimeException ex) {
            // Bắt lỗi nếu gõ sai Mã hóa đơn không tồn tại trong hệ thống
            model.addAttribute("error", ex.getMessage());
        }

        model.addAttribute("histories", histories);
        return "shared/booking-history";
    }
}
