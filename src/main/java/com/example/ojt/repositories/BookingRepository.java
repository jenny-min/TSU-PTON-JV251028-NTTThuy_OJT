package com.example.ojt.repositories;

import com.example.ojt.entities.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUser_Username(String username);

    List<Booking> findByShowtimeShowtimeId(Long showtimeId);

    //Query join booking->showtime->movie->room->ticket
    @Query("""
        SELECT DISTINCT b
        FROM Booking b
        JOIN FETCH b.showtime s
        JOIN FETCH s.movie
        JOIN FETCH s.room
        LEFT JOIN FETCH b.tickets
        WHERE b.user.id = :userId
        ORDER BY b.bookingDate DESC
    """)
    List<Booking> findHistoryByUser(Long userId);

    // Tính tổng doanh thu từ các đơn đặt vé thành công
    @Query("SELECT SUM(b.totalAmount) FROM Booking b WHERE b.bookingStatus = 'SUCCESS'")
    BigDecimal calculateTotalRevenue();

    // Lấy danh sách 10 đơn đặt vé mới nhất hiển thị lên bảng
    List<Booking> findTop10ByOrderByBookingDateDesc();

    // Thống kê doanh thu theo 7 ngày gần nhất để vẽ biểu đồ
    // Trả về một List danh sách mảng Object [Ngày, DoanhThu]
    @Query(value = "SELECT DATE(b.booking_date) AS date, SUM(b.total_amount) AS revenue " +
            "FROM booking b WHERE b.booking_status = 'SUCCESS' " +
            "GROUP BY DATE(b.booking_date) " +
            "ORDER BY DATE(b.booking_date) DESC LIMIT 7", nativeQuery = true)
    List<Object[]> getRevenueLast7Days();
}
