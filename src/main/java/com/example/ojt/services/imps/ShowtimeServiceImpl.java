package com.example.ojt.services.imps;

import com.example.ojt.dtos.showtime.CreateShowtimeRequest;
import com.example.ojt.dtos.showtime.ShowtimeResponse;
import com.example.ojt.dtos.showtime.UpdateShowtimeRequest;
import com.example.ojt.entities.Movie;
import com.example.ojt.entities.Room;
import com.example.ojt.entities.Showtime;
import com.example.ojt.enums.BookingStatus;
import com.example.ojt.repositories.MovieRepository;
import com.example.ojt.repositories.RoomRepository;
import com.example.ojt.repositories.ShowtimeRepository;
import com.example.ojt.enums.ShowtimeStatus;
import com.example.ojt.services.interfaces.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShowtimeServiceImpl implements ShowtimeService {
    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;

    //Thời gian dọn phòng
    private final int cleaningTime = 15;

    @Override
    public Page<ShowtimeResponse> getShowtimes(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return showtimeRepository.findAll(pageable)
                .map(this::toResponse);
    }

    @Override
    public ShowtimeResponse getShowtimeById(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() ->new RuntimeException("Không tìm thấy xuất chiếu"));

        return toResponse(showtime);
    }

    //Tạo suất chiếu
    @Override
    public ShowtimeResponse createShowtime(CreateShowtimeRequest request) {

        Movie movie = getMovie(request.getMovieId());
        Room room = getRoom(request.getRoomId());

        LocalDateTime endTime = calcEndTime(request.getStartTime(), movie);

        validateConflict(room.getRoomId(), request.getStartTime(), endTime, null);

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(request.getStartTime());
        showtime.setEndTime(endTime);
        showtime.setTicketPrice(request.getTicketPrice());
        showtime.setStatus(ShowtimeStatus.DRAFT);

        return toResponse(showtimeRepository.save(showtime));
    }


    //Cập nhật xuất chiêu
    @Override
    public UpdateShowtimeRequest getShowtimeForUpdate(Long id) {

        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));

        if (showtime.getStatus() != ShowtimeStatus.DRAFT) {
            throw new IllegalStateException("Chỉ draft mới được chỉnh sửa");
        }

        UpdateShowtimeRequest request = new UpdateShowtimeRequest();

        request.setMovieId(showtime.getMovie().getMovieId());
        request.setRoomId(showtime.getRoom().getRoomId());
        request.setStartTime(showtime.getStartTime());
        request.setTicketPrice(showtime.getTicketPrice());

        return request;
    }

    @Override
    public ShowtimeResponse updateShowtime(Long id, UpdateShowtimeRequest request) {

        Showtime showtime = getShowtime(id);

        if (showtime.getStatus() == ShowtimeStatus.PUBLISHED) {
            throw new IllegalStateException("Showtime đã publish, không thể chỉnh sửa");
        }

        Movie movie = getMovie(request.getMovieId());
        Room room = getRoom(request.getRoomId());

        LocalDateTime endTime = calcEndTime(request.getStartTime(), movie);

        validateConflict(room.getRoomId(), request.getStartTime(), endTime, id);

        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(request.getStartTime());
        showtime.setEndTime(endTime);
        showtime.setTicketPrice(request.getTicketPrice());

        return toResponse(showtimeRepository.save(showtime));
    }

    //Xóa suất chiếu
    @Override
    public void deleteShowtime(Long id) {
        Showtime showtime = getShowtime(id);

        if (showtime.getStatus() == ShowtimeStatus.PUBLISHED) {
            throw new IllegalStateException("Không thể xóa showtime đã publish");
        }

        showtimeRepository.delete(showtime);
    }

    @Override
    public void publish(Long id) {
        Showtime showtime = getShowtime(id);

        if (showtime.getStatus() != ShowtimeStatus.DRAFT) {
            throw new IllegalStateException("Chỉ draft mới được publish");
        }

        if (showtime.getStartTime().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Không thể publish suất chiếu đã bắt đầu.");
        }

        showtime.setStatus(ShowtimeStatus.PUBLISHED);

        showtimeRepository.save(showtime);
    }

    //Kiểm tra phòng còn trống hay không, chống xung đột
    private void validateRoomConflict(
            Long roomId,
            LocalDateTime startTime,
            LocalDateTime endTime,
            Long currentShowtimeId) {

        List<Showtime> showtimes =
                showtimeRepository.findByRoomRoomId(roomId);

        for (Showtime existing : showtimes) {

            if (currentShowtimeId != null &&
                    existing.getShowtimeId().equals(currentShowtimeId)) {
                continue;
            }

            boolean conflict =
                    startTime.isBefore(existing.getEndTime())
                            && endTime.isAfter(existing.getStartTime());

            if (conflict) {
                throw new RuntimeException(
                        "Phòng chiếu đã có lịch trong khoảng thời gian này");
            }
        }
    }

    @Override
    public List<ShowtimeResponse> getByMovieId(Long movieId) {
        return showtimeRepository.findByMovieMovieId(movieId)
                .stream()
                .filter(s -> s.getStatus() == ShowtimeStatus.PUBLISHED)
                .filter(s -> s.getStartTime().isAfter(LocalDateTime.now()))
                .map(this::toResponse)
                .toList();
    }

    //Mapper
    private ShowtimeResponse toResponse(Showtime showtime) {

        boolean expired = showtime.getStartTime().isBefore(LocalDateTime.now());

        boolean soldOut = isSoldOut(showtime);

        return ShowtimeResponse.builder()
                .showtimeId(showtime.getShowtimeId())
                .movieId(showtime.getMovie().getMovieId())
                .movieTitle(showtime.getMovie().getTitle())
                .roomId(showtime.getRoom().getRoomId())
                .roomName(showtime.getRoom().getRoomName())
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .ticketPrice(showtime.getTicketPrice())
                .status(showtime.getStatus())
                .expired(expired)
                .soldOut(soldOut)
                .build();
    }

    private boolean isSoldOut(Showtime showtime) {
        int bookedSeats = showtime.getBookings()
                .stream()
                .filter(booking ->
                        booking.getBookingStatus() == BookingStatus.PAID)
                .mapToInt(booking ->
                        booking.getBookingSeat().split(",").length)
                .sum();

        return bookedSeats >= showtime.getRoom().getTotalSeats();
    }

    private Showtime getShowtime(Long id) {
        return showtimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy showtime"));
    }

    private Movie getMovie(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));
    }

    private Room getRoom(Long id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));
    }

    private LocalDateTime calcEndTime(LocalDateTime start, Movie movie) {
        return start.plusMinutes(movie.getDuration() + cleaningTime);
    }

    private void validateConflict(Long roomId, LocalDateTime start, LocalDateTime end, Long id) {
        long count = showtimeRepository.countConflicts(roomId, id, start, end);

        if (count > 0) {
            throw new RuntimeException("Phòng bị trùng lịch");
        }
    }
}
