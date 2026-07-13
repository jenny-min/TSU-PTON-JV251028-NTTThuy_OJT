package com.example.ojt.controllers;

import com.example.ojt.services.interfaces.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminDashboardService adminDashboardService;
    //Vào dashboard admin
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Gán các thông số Stats
        model.addAttribute("totalRevenue", adminDashboardService.getTotalRevenue());
        model.addAttribute("totalTicketsSold", adminDashboardService.getTotalTicketsSold());
        model.addAttribute("activeMoviesCount", adminDashboardService.getActiveMoviesCount());
        model.addAttribute("activeUsersCount", adminDashboardService.getActiveUsersCount());
        model.addAttribute("disabledUsersCount", adminDashboardService.getDisabledUsersCount());

        // Gán cấu trúc bảng & danh sách phòng
        model.addAttribute("rooms", adminDashboardService.getAllRooms());
        model.addAttribute("recentBookings", adminDashboardService.getRecentBookings());
        model.addAttribute("upcomingShowtimes", adminDashboardService.getUpcomingShowtimes());

        // Xử lý chuyển đổi Map dữ liệu biểu đồ sang chuỗi JSON cho file JS độc lập đọc
//        Map<String, Object> chartData = adminDashboardService.getRevenueChartData();
//        try {
//            String chartDataJson = objectMapper.writeValueAsString(chartData);
//            model.addAttribute("chartDataModelJson", chartDataJson);
//        } catch (JsonProcessingException e) {
//            model.addAttribute("chartDataModelJson", "{\"labels\":[], \"values\":[]}");
//        }
        return "admin/dashboard";
    }
}
