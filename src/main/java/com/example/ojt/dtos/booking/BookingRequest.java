package com.example.ojt.dtos.booking;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BookingRequest {
    @NotNull(message = "Suất chiếu không được để trống")
    private Long showtimeId;

    @NotBlank(message = "Ghế không được để trống")
    private String bookingSeat;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    private String paymentMethod;
}
