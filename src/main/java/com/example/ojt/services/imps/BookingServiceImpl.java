package com.example.ojt.services.imps;

import com.example.ojt.dtos.booking.*;
import com.example.ojt.entities.*;
import com.example.ojt.repositories.BookingRepository;
import com.example.ojt.repositories.ShowtimeRepository;
import com.example.ojt.repositories.TicketRepository;
import com.example.ojt.repositories.UserRepository;
import com.example.ojt.enums.BookingStatus;
import com.example.ojt.services.interfaces.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ConfirmBookingResponse getConfirmBooking(ConfirmBookingRequest request) {

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu"));

        Movie movie = showtime.getMovie();
        Room room = showtime.getRoom();

        String bookingSeat = request.getBookingSeat();

        if (bookingSeat == null || bookingSeat.isBlank()) {
            throw new RuntimeException("Ghế không được để trống");
        }

        List<String> seatCodes = Arrays.stream(bookingSeat.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        int seatCount = seatCodes.size();

        BigDecimal totalPrice =
                showtime.getTicketPrice()
                        .multiply(BigDecimal.valueOf(seatCount));

        ConfirmBookingResponse response = new ConfirmBookingResponse();
        response.setMovie(movie);
        response.setShowtime(showtime);
        response.setRoom(room);
        response.setSeatCodes(seatCodes);
        response.setBookingSeat(String.join(",", seatCodes));
        response.setSeatCount((long) seatCount);
        response.setTotalPrice(totalPrice);

        return response;
    }

    @Override
    public ConfirmBookingResponse buildConfirm(BookingRequest request) {

        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));

        List<String> seatCodes = Arrays.stream(request.getBookingSeat().split(","))
                .map(String::trim)
                .toList();

        BigDecimal totalPrice = showtime.getTicketPrice()
                .multiply(BigDecimal.valueOf(seatCodes.size()));

        ConfirmBookingResponse response = new ConfirmBookingResponse();
        response.setMovie(showtime.getMovie());
        response.setShowtime(showtime);
        response.setRoom(showtime.getRoom());
        response.setSeatCodes(seatCodes);
        response.setBookingSeat(String.join(",", seatCodes));
        response.setSeatCount((long) seatCodes.size());
        response.setTotalPrice(totalPrice);
        return response;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public TicketResponse checkout(BookingRequest request, String username) {

        // 1. Lấy người dùng
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        // 2. Lấy suất chiếu
        Showtime showtime = showtimeRepository.findById(request.getShowtimeId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));

        // 3. Danh sách ghế
        List<String> seatCodes = Arrays.stream(request.getBookingSeat().split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .toList();

        // 4. Kiểm tra ghế đã được đặt chưa
        for (String seatCode : seatCodes) {

            if (ticketRepository.existsByShowtimeAndSeatCode(showtime, seatCode)) {
                throw new RuntimeException("Ghế " + seatCode + " đã được người khác đặt.");
            }
        }

        // 5. Tính tổng tiền
        BigDecimal totalAmount = showtime.getTicketPrice()
                .multiply(BigDecimal.valueOf(seatCodes.size()));

        // 6. Tạo Booking
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setBookingDate(LocalDateTime.now());
        booking.setBookingSeat(String.join(",", seatCodes));
        booking.setPaymentMethod(request.getPaymentMethod());
        booking.setTotalAmount(totalAmount);

        // Thanh toán tại quầy -> chưa thanh toán
        booking.setBookingStatus(BookingStatus.PENDING);

        booking = bookingRepository.save(booking);

        // 7. Tạo Ticket
        List<Ticket> tickets = new ArrayList<>();

        for (String seatCode : seatCodes) {

            Ticket ticket = new Ticket();
            ticket.setBooking(booking);
            ticket.setShowtime(showtime);
            ticket.setSeatCode(seatCode);
            ticket.setPrice(showtime.getTicketPrice());

            tickets.add(ticket);
        }

        ticketRepository.saveAll(tickets);

        booking.setTickets(tickets);

        // 8. Trả về DTO
        TicketResponse response = new TicketResponse();

        response.setBookingId(booking.getBookingId());
        response.setMovieTitle(showtime.getMovie().getTitle());
        response.setRoomName(showtime.getRoom().getRoomName());
        response.setStartTime(showtime.getStartTime());
        response.setEndTime(showtime.getEndTime());
        response.setBookingSeat(booking.getBookingSeat());
        response.setTotalAmount(booking.getTotalAmount());
        response.setPaymentMethod(booking.getPaymentMethod());
        response.setBookingDate(booking.getBookingDate());
        response.setBookingStatus(booking.getBookingStatus());

        return response;
    }

    @Override
    public List<TicketResponse> getBookingHistory(Long userId) {

        List<Booking> bookings =
                bookingRepository.findHistoryByUser(userId);

        return bookings.stream()
                .map(this::toTicketResponse)
                .toList();
    }

    //Mapper ticketResponse
    private TicketResponse toTicketResponse(Booking booking) {

        TicketResponse response = new TicketResponse();

        response.setBookingId(booking.getBookingId());

        response.setMovieTitle(
                booking.getShowtime().getMovie().getTitle());

        response.setRoomName(
                booking.getShowtime().getRoom().getRoomName());

        response.setStartTime(
                booking.getShowtime().getStartTime());

        response.setEndTime(
                booking.getShowtime().getEndTime());

        response.setBookingSeat(
                booking.getBookingSeat());

        response.setTotalAmount(
                booking.getTotalAmount());

        response.setPaymentMethod(
                booking.getPaymentMethod());

        response.setBookingDate(
                booking.getBookingDate());

        return response;
    }
}
