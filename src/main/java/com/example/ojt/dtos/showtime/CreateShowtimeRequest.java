package com.example.ojt.dtos.showtime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateShowtimeRequest {
    @NotNull(message = "Phim không được để trống")
    private Long movieId;

    @NotNull(message = "Phòng chiếu không được để trống")
    private Long roomId;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @NotNull(message = "Thời gian bắt đầu không được để trống")
    private LocalDateTime startTime;

    @NotNull(message = "Giá vé không được để trống")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Giá vé phải lớn hơn 0"
    )
    private BigDecimal ticketPrice;
}
