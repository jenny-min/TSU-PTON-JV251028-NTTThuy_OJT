package com.example.ojt.services.interfaces;

import com.example.ojt.dtos.booking.BookingResponse;
import com.example.ojt.dtos.booking.CreateBookingRequest;
import com.example.ojt.entities.Booking;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(String username, CreateBookingRequest request);

    List<BookingResponse> getMyBookings(String username);

    BookingResponse mapToResponse(Booking booking);
}
