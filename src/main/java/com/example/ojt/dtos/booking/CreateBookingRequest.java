package com.example.ojt.dtos.booking;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookingRequest {
    private Long showtimeId;
    private List<String> seatCodes;
}
