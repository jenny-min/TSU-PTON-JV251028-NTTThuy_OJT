package com.example.ojt.dtos.booking;

import com.example.ojt.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BookingHistoryResponse {
    private Long bookingId;
    private String movieTitle;
    private String roomName;
    private LocalDateTime showtime;
    private List<String> seats;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private BookingStatus bookingStatus;
    private LocalDateTime bookingDate;
}
