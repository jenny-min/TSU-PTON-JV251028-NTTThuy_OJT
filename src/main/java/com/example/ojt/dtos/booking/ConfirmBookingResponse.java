package com.example.ojt.dtos.booking;

import com.example.ojt.entities.Movie;
import com.example.ojt.entities.Room;
import com.example.ojt.entities.Showtime;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ConfirmBookingResponse {
    private Movie movie;
    private Showtime showtime;
    private Room room;
    private List<String> seatCodes;
    private String bookingSeat;
    private Long seatCount;
    private BigDecimal totalPrice;
}
