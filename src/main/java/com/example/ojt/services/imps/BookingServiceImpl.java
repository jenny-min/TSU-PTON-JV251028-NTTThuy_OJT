package com.example.ojt.services.imps;

import com.example.ojt.dtos.booking.BookingHistoryResponse;
import com.example.ojt.dtos.booking.BookingResponse;
import com.example.ojt.dtos.booking.CreateBookingRequest;
import com.example.ojt.entities.Booking;
import com.example.ojt.entities.Showtime;
import com.example.ojt.entities.Ticket;
import com.example.ojt.entities.User;
import com.example.ojt.repositories.BookingRepository;
import com.example.ojt.repositories.ShowtimeRepository;
import com.example.ojt.repositories.TicketRepository;
import com.example.ojt.repositories.UserRepository;
import com.example.ojt.roles.BookingStatus;
import com.example.ojt.services.interfaces.BookingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final UserRepository userRepository;
    private final ShowtimeRepository showtimeRepository;
    private final BookingRepository bookingRepository;
    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public BookingResponse createBooking(String username, CreateBookingRequest request) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new RuntimeException("Showtime not found"));

        // Kiểm tra ghế đã được đặt chưa
        for (String seatCode : request.getSeatCodes()) {

            if (ticketRepository.existsByShowtimeAndSeatCode(showtime, seatCode)) {
                throw new RuntimeException("Seat " + seatCode + " has already been booked.");
            }
        }

        // Tạo Booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setBookingSeat(String.join(",", request.getSeatCodes()));

        BigDecimal total = showtime.getTicketPrice()
                .multiply(BigDecimal.valueOf(request.getSeatCodes().size()));

        booking.setTotalAmount(total);
        booking.setBookingStatus(BookingStatus.PENDING);
        booking.setBookingDate(LocalDateTime.now());

        booking = bookingRepository.save(booking);

        // Tạo Ticket
        List<Ticket> tickets = new ArrayList<>();

        for (String seatCode : request.getSeatCodes()) {

            Ticket ticket = Ticket.builder()
                    .booking(booking)
                    .showtime(showtime)
                    .seatCode(seatCode)
                    .price(showtime.getTicketPrice())
                    .build();

            tickets.add(ticket);
        }

        ticketRepository.saveAll(tickets);

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

    @Override
    public List<BookingHistoryResponse> getBookingHistory(Long userId) {
        List<Booking> bookings =
                bookingRepository.findHistoryByUser(userId);

        return bookings.stream().map(booking -> {

            BookingHistoryResponse dto = new BookingHistoryResponse();

            dto.setBookingId(booking.getBookingId());
            dto.setMovieTitle(booking.getShowtime().getMovie().getTitle());
            dto.setRoomName(booking.getShowtime().getRoom().getRoomName());
            dto.setShowtime(booking.getShowtime().getStartTime());
            dto.setBookingDate(booking.getBookingDate());
            dto.setTotalAmount(booking.getTotalAmount());
            dto.setPaymentMethod(booking.getPaymentMethod());
            dto.setBookingStatus(booking.getBookingStatus());
            dto.setSeats(booking.getTickets().stream().map(Ticket::getSeatCode).toList()
            );

            return dto;

        }).toList();
    }
}
