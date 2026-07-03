package com.example.ojt.dtos.booking;

import com.example.ojt.entities.Showtime;
import com.example.ojt.entities.User;
import com.example.ojt.enums.BookingStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingResponse {
    private Long bookingId;
    private User user;
    private Showtime showtime;
    private LocalDateTime bookingDate;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private BookingStatus bookingStatus;
    private String bookingSeat;
    private List<String> seatCodes;
}
