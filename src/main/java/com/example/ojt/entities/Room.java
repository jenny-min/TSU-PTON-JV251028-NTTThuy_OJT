package com.example.ojt.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @NotBlank(message = "Tên phòng không được để trống")
    @Size(max = 50, message = "Tên phòng không được vượt quá 50 ký tự")
    @Column(nullable = false, length = 50)
    private String roomName;

    @Min(value = 1, message = "Tổng số ghế phải lớn hơn 0")
    private int totalSeats;

    @Min(value = 1, message = "Số hàng ghế phải lớn hơn 0")
    private int seatsX;

    @Min(value = 1, message = "Số cột ghế phải lớn hơn 0")
    private int seatsY;

    @Size(max = 255, message = "Danh sách ghế VIP không được vượt quá 255 ký tự")
    private String vipSeats;

    @Size(max = 255, message = "Danh sách ghế Couple không được vượt quá 255 ký tự")
    private String coupleSeats;

    @NotNull(message = "Trạng thái phòng không được để trống")
    private Boolean status;

    @OneToMany(mappedBy = "room")
    private List<Showtime> showtimes;
}
