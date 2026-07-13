package com.example.ojt.services.imps;

import com.example.ojt.entities.Booking;
import com.example.ojt.entities.Room;
import com.example.ojt.entities.Showtime;
import com.example.ojt.enums.MovieStatus;
import com.example.ojt.repositories.*;
import com.example.ojt.services.interfaces.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {
    private final BookingRepository bookingRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final ShowtimeRepository showtimeRepository;

    @Override
    public BigDecimal getTotalRevenue() {
        BigDecimal revenue = bookingRepository.calculateTotalRevenue();
        return revenue != null ? revenue : BigDecimal.ZERO;
    }

    @Override
    public long getTotalTicketsSold() {
        // Đếm tổng số lượng vé từ các Booking thành công
        return bookingRepository.findTop10ByOrderByBookingDateDesc().stream()
                .filter(b -> b.getBookingStatus().name().equals("SUCCESS"))
                .mapToInt(b -> b.getTickets() != null ? b.getTickets().size() : 0)
                .sum();
    }

    @Override
    public long getActiveMoviesCount() {
        return movieRepository.countByStatus(MovieStatus.NOW_SHOWING);
    }

    @Override
    public long getActiveUsersCount() {
        return userRepository.countByEnabled(true);
    }

    @Override
    public long getDisabledUsersCount() {
        return userRepository.countByEnabled(false);
    }

    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public List<Booking> getRecentBookings() {
        return bookingRepository.findTop10ByOrderByBookingDateDesc();
    }

    @Override
    public List<Showtime> getUpcomingShowtimes() {
        return showtimeRepository.findTop10ByStartTimeAfterOrderByStartTimeAsc(LocalDateTime.now());
    }

    @Override
    public Map<String, Object> getRevenueChartData() {
        List<Object[]> rawData = bookingRepository.getRevenueLast7Days();

        List<String> labels = new ArrayList<>();
        List<BigDecimal> values = new ArrayList<>();

        // Vì SQL đảo ngược từ ngày mới nhất về trước,đảo ngược ngược lại để hiển thị biểu đồ từ trái sang phải hợp lý hơn
        Collections.reverse(rawData);

        for (Object[] row : rawData) {
            labels.add(row[0].toString());             // Ngày (YYYY-MM-DD)
            values.add((BigDecimal) row[1]);           // Tổng doanh thu ngày đó
        }

        Map<String, Object> chartData = new HashMap<>();
        chartData.put("labels", labels);
        chartData.put("values", values);

        return chartData;
    }
}
