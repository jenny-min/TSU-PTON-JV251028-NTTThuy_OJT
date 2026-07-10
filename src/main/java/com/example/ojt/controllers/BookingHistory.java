package com.example.ojt.controllers;

import com.example.ojt.dtos.booking.TicketResponse;
import com.example.ojt.entities.User;
import com.example.ojt.services.interfaces.BookingService;
import com.example.ojt.services.interfaces.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class BookingHistory {
    private final UserService userService;
    private final BookingService bookingService;

    @GetMapping("/bookings/history")
    public String bookingHistory(Model model,
                                 Principal principal,
                                 HttpServletRequest request) {

        // Lấy thông tin người dùng đang đăng nhập
        User user = userService.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        List<TicketResponse> histories;

        // Phân luồng dữ liệu dựa trên Role
        if (request.isUserInRole("ADMIN") || request.isUserInRole("STAFF")) {
            // Nếu là Admin/Staff: Lấy TẤT CẢ hóa đơn/vé trong hệ thống
            histories = bookingService.getAllBookings();
        } else {
            // Nếu là Khách hàng (User): Chỉ lấy lịch sử của cá nhân
            histories = bookingService.getBookingHistory(user.getId());
        }

        model.addAttribute("histories", histories);
        return "shared/booking-history";
    }
}
