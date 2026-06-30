package com.example.ojt.services.imps;

import com.example.ojt.dtos.showtime.CreateShowtimeRequest;
import com.example.ojt.dtos.showtime.ShowtimeResponse;
import com.example.ojt.dtos.showtime.UpdateShowtimeRequest;
import com.example.ojt.entities.Movie;
import com.example.ojt.entities.Room;
import com.example.ojt.entities.Showtime;
import com.example.ojt.repositories.MovieRepository;
import com.example.ojt.repositories.RoomRepository;
import com.example.ojt.repositories.ShowtimeRepository;
import com.example.ojt.roles.ShowtimeStatus;
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
        //Tìm phim và phòng
        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim"));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng"));

        LocalDateTime endTime = request.getStartTime()
                .plusMinutes(movie.getDuration() + cleaningTime);

        validateRoomConflict(
                room.getRoomId(),
                request.getStartTime(),
                endTime,
                null
        );

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(request.getStartTime());
        showtime.setEndTime(endTime);
        showtime.setTicketPrice(request.getTicketPrice());
        showtime.setStatus(ShowtimeStatus.UPCOMING);


        return toResponse(showtimeRepository.save(showtime));
    }


    //Cập nhật xuất chiêu
    @Override
    public UpdateShowtimeRequest getShowtimeForUpdate(Long id) {

        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy suất chiếu"));

        UpdateShowtimeRequest request = new UpdateShowtimeRequest();

        request.setMovieId(showtime.getMovie().getMovieId());
        request.setRoomId(showtime.getRoom().getRoomId());
        request.setStartTime(showtime.getStartTime());
        request.setTicketPrice(showtime.getTicketPrice());

        return request;
    }

    @Override
    public ShowtimeResponse updateShowtime(Long id, UpdateShowtimeRequest request) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu"));

        Movie movie = movieRepository.findById(request.getMovieId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy phim"));

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy phòng"));

        LocalDateTime endTime =
                request.getStartTime()
                        .plusMinutes(movie.getDuration() + cleaningTime);

        validateRoomConflict(
                room.getRoomId(),
                request.getStartTime(),
                endTime,
                id
        );

        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(request.getStartTime());
        showtime.setEndTime(endTime);
        showtime.setTicketPrice(request.getTicketPrice());

        return toResponse(
                showtimeRepository.save(showtime)
        );
    }

    //Xóa suất chiếu
    @Override
    public void deleteShowtime(Long id) {
        showtimeRepository.deleteById(id);
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

    private ShowtimeResponse toResponse(Showtime showtime) {
        return ShowtimeResponse.builder()
                .showtimeId(showtime.getShowtimeId())
                .movie(showtime.getMovie())
                .room(showtime.getRoom())
                .startTime(showtime.getStartTime())
                .endTime(showtime.getEndTime())
                .ticketPrice(showtime.getTicketPrice())
                .status(showtime.getStatus())
//                .bookings(showtime.getBookings())
                .build();
    }

    @Override
    public List<ShowtimeResponse> getByMovieId(Long movieId) {

        List<Showtime> showtimes =
                showtimeRepository.findByMovieMovieId(movieId);

        return showtimes.stream()
                .map(this::toResponse)
                .toList();
    }
}
