package com.example.ojt.entities;

import com.example.ojt.enums.BookingStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "bookings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookingId;

    @NotNull(message = "Người dùng không được để trống")
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Suất chiếu không được để trống")
    @ManyToOne
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    private LocalDateTime bookingDate;

    @NotNull(message = "Tổng tiền không được để trống")
    @DecimalMin(
            value = "0.0",
            inclusive = false,
            message = "Tổng tiền phải lớn hơn 0"
    )
    private BigDecimal totalAmount;

    @NotBlank(message = "Phương thức thanh toán không được để trống")
    @Size(max = 50, message = "Phương thức thanh toán không được vượt quá 50 ký tự")
    private String paymentMethod;

    @NotNull(message = "Trạng thái đơn đặt vé không được để trống")
    @Enumerated(EnumType.STRING)
    private BookingStatus bookingStatus;

    @NotBlank(message = "Ghế đặt không được để trống")
    @Column(length = 255)
    private String bookingSeat;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL)
    private List<Ticket> tickets;
}
