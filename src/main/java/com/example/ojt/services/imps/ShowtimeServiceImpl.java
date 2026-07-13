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
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ShowtimeServiceImpl implements ShowtimeService {
    private final ShowtimeRepository showtimeRepository;
    private final MovieRepository movieRepository;
    private final RoomRepository roomRepository;

    //Thời gian dọn phòng
    private final int cleaningTime = 15;

    @Override
    public Page<ShowtimeResponse> getShowtimes(Long movieId, Long roomId, LocalDate date, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        // Gọi repository chứa câu lệnh Query phân trang kết hợp filter
        Page<Showtime> showtimePage = showtimeRepository.filterShowtimes(movieId, roomId, date, pageable);

        // Dịch mượt mà từ Page<Showtime> sang Page<ShowtimeResponse> nhờ hàm helper có sẵn của bạn
        return showtimePage.map(this::toResponse);
    }

    @Override
    public List<ShowtimeResponse> getAllShowtimes() {
        // 1. Lấy tất cả suất chiếu từ Database, sắp xếp theo thời gian bắt đầu từ gần nhất đến xa nhất
        List<Showtime> showtimes = showtimeRepository.findAllByOrderByStartTimeAsc();

        // 2. Sử dụng hàm helper toResponse để chuyển đổi mượt mà toàn bộ danh sách sang DTO
        return showtimes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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

    @Override
    public List<ShowtimeResponse> getUpcomingShowtimesByMovieId(Long movieId) {
        LocalDateTime now = LocalDateTime.now();

        List<Showtime> upcomingShowtimes = showtimeRepository.findUpcomingByMovieId(movieId, now);

        return upcomingShowtimes.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
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
                .movieId(showtime.getMovie() != null ? showtime.getMovie().getMovieId() : null)
                .movieTitle(showtime.getMovie() != null ? showtime.getMovie().getTitle() : "N/A")
                .roomId(showtime.getRoom() != null ? showtime.getRoom().getRoomId() : null)
                .roomName(showtime.getRoom() != null ? showtime.getRoom().getRoomName() : "N/A")
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .ticketPrice(showtime.getTicketPrice())
                .status(showtime.getStatus())
                .expired(expired)
                .soldOut(soldOut)
                .build();
    }

    private boolean isSoldOut(Showtime showtime) {
        if (showtime.getBookings() == null || showtime.getRoom() == null) {
            return false;
        }

        int bookedSeats = showtime.getBookings()
                .stream()
                // Hãy đổi sang trạng thái chuẩn của hệ thống (PAID hoặc SUCCESS)
                .filter(b -> b.getBookingStatus() == BookingStatus.PAID)
                .mapToInt(b -> {
                    if (b.getBookingSeat() == null || b.getBookingSeat().isEmpty()) {
                        return 0;
                    }
                    // Tách chuỗi ghế dạng "A1,A2" thành mảng để đếm số lượng ghế
                    return b.getBookingSeat().split(",").length;
                })
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
