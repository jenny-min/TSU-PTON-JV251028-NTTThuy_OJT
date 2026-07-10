package com.example.ojt.dtos.booking;

import com.example.ojt.entities.User;
import com.example.ojt.enums.BookingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketResponse {
    private Long bookingId;
    private User user;
    private String movieTitle;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String bookingSeat;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private LocalDateTime bookingDate;
    private BookingStatus bookingStatus;
    private BigDecimal ticketPrice;
    private Long seatCount;
}
