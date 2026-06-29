package com.example.ojt.dtos.booking;

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
    private String movieTitle;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String bookingSeat;
    private BigDecimal totalAmount;
    private String paymentMethod;
    private LocalDateTime bookingDate;
}
