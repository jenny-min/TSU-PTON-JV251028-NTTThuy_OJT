package com.example.ojt.dtos.showtime;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateShowtimeRequest {
    @NotNull
    private Long movieId;

    @NotNull
    private Long roomId;

    @NotNull
    private LocalDateTime startTime;

    @NotNull
    @DecimalMin(
            value = "0.0",
            inclusive = false
    )
    private BigDecimal ticketPrice;
}
