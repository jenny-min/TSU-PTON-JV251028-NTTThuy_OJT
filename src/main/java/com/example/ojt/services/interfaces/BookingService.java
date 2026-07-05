package com.example.ojt.services.interfaces;

import com.example.ojt.dtos.booking.*;
import com.example.ojt.entities.Booking;

import java.util.List;
import java.util.Set;

public interface BookingService {
    BookingResponse createBooking(String username, CreateBookingRequest request);

    List<BookingResponse> getMyBookings(String username);

    BookingResponse mapToResponse(Booking booking);

    List<TicketResponse> getBookingHistory(Long userId);

    ConfirmBookingResponse getConfirmBooking(ConfirmBookingRequest request);

    ConfirmBookingResponse buildConfirm(BookingRequest request);

    Set<String> getBookedSeats(Long showtimeId);

    TicketResponse checkout(BookingRequest request, String email);
}
