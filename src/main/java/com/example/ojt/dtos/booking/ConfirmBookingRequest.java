package com.example.ojt.dtos.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ConfirmBookingRequest {
    @NotNull(message = "Suất chiếu không được để trống")
    private Long showtimeId;

    @NotBlank(message = "Ghế không được để trống")
    private String bookingSeat;
}
