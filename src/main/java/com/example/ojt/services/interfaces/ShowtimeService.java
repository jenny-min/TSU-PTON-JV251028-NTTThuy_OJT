package com.example.ojt.services.interfaces;

import com.example.ojt.dtos.showtime.CreateShowtimeRequest;
import com.example.ojt.dtos.showtime.ShowtimeResponse;
import com.example.ojt.dtos.showtime.UpdateShowtimeRequest;
import com.example.ojt.entities.Movie;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

public interface ShowtimeService{

    Page<ShowtimeResponse> getShowtimes(Long movieId, Long roomId, LocalDate date, int page, int size);

    List<ShowtimeResponse> getAllShowtimes();

    ShowtimeResponse getShowtimeById(Long id);

    UpdateShowtimeRequest getShowtimeForUpdate(Long id);

    ShowtimeResponse createShowtime(CreateShowtimeRequest request);

    ShowtimeResponse updateShowtime(Long id, UpdateShowtimeRequest request);

    void deleteShowtime(Long id);

    List<ShowtimeResponse> getByMovieId(Long movieId);

    void publish(Long id);

    List<ShowtimeResponse> getUpcomingShowtimesByMovieId(Long movieId);
}
