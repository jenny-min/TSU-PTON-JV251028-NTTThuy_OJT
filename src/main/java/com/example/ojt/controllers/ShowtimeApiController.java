package com.example.ojt.controllers;

import com.example.ojt.dtos.showtime.ShowtimeResponse;
import com.example.ojt.services.interfaces.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class ShowtimeApiController {
    private final ShowtimeService showtimeService;

    @GetMapping("/showtimes")
    @ResponseBody
    public List<ShowtimeResponse> getShowtimesByMovie(@RequestParam("movieId") Long movieId) {
        return showtimeService.getUpcomingShowtimesByMovieId(movieId);
    }

    @GetMapping("/schedules")
    public String bookingSchedules(Model model) {
        try {
            List<ShowtimeResponse> showtimes = showtimeService.getAllShowtimes();
            model.addAttribute("showtimes", showtimes);

            // Tạo danh sách 7 ngày tiếp theo để làm thanh chọn lịch
            List<LocalDate> weekDates = new ArrayList<>();
            LocalDate today = LocalDate.now();
            for (int i = 0; i < 7; i++) {
                weekDates.add(today.plusDays(i));
            }
            model.addAttribute("weekDates", weekDates);

            // Định dạng ngày tiếng Việt gửi qua view làm Helper (Thứ/Ngày/Tháng)
            model.addAttribute("dateFormatter", DateTimeFormatter.ofPattern("dd/MM"));
            model.addAttribute("dayFormatter", DateTimeFormatter.ofPattern("E", new Locale("vi", "VN")));

        } catch (Exception e) {
            model.addAttribute("error", "Không thể tải danh sách lịch chiếu: " + e.getMessage());
        }

        return "shared/booking-schedule";
    }
}
