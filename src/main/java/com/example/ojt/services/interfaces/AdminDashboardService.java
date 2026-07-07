package com.example.ojt.services.interfaces;

import com.example.ojt.entities.Booking;
import com.example.ojt.entities.Room;
import com.example.ojt.entities.Showtime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface AdminDashboardService {
    BigDecimal getTotalRevenue();
    long getTotalTicketsSold();
    long getActiveMoviesCount();
    long getActiveUsersCount();
    long getDisabledUsersCount();
    List<Room> getAllRooms();
    List<Booking> getRecentBookings();
    List<Showtime> getUpcomingShowtimes();
    Map<String, Object> getRevenueChartData(); // Hàm xử lý cấu trúc biểu đồ JSON
}
