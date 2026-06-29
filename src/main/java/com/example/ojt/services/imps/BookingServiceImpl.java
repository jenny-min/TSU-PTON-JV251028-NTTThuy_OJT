package com.example.ojt.services.imps;

import com.example.ojt.dtos.booking.BookingResponse;
import com.example.ojt.dtos.booking.CreateBookingRequest;
import com.example.ojt.entities.Booking;
import com.example.ojt.entities.Showtime;
import com.example.ojt.entities.User;
import com.example.ojt.repositories.BookingRepository;
import com.example.ojt.repositories.ShowtimeRepository;
import com.example.ojt.repositories.UserRepository;
import com.example.ojt.roles.BookingStatus;
import com.example.ojt.services.interfaces.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ShowtimeRepository showtimeRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingResponse createBooking(String username, CreateBookingRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow();

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow();

        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setBookingSeat(String.join(",", request.getSeatCodes()));

        BigDecimal total = showtime.getTicketPrice()
                .multiply(BigDecimal.valueOf(request.getSeatCodes().size()));

        booking.setTotalAmount(total);
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setBookingDate(LocalDateTime.now());

        bookingRepository.save(booking);

        return mapToResponse(booking);
    }

    @Override
    public List<BookingResponse> getMyBookings(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Booking> bookings = bookingRepository.findByUser_Username(username);

        return bookings.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public BookingResponse mapToResponse(Booking booking) {
        BookingResponse res = new BookingResponse();
        res.setBookingId(booking.getBookingId());
        res.setSeatCodes(Arrays.asList(booking.getBookingSeat().split(",")));
        res.setTotalAmount(booking.getTotalAmount());
        res.setBookingStatus(booking.getBookingStatus());

        return res;
    }
}
