package com.example.ojt.services.interfaces;

import com.example.ojt.dtos.showtime.CreateShowtimeRequest;
import com.example.ojt.dtos.showtime.ShowtimeResponse;
import com.example.ojt.dtos.showtime.UpdateShowtimeRequest;
import org.springframework.data.domain.Page;

public interface ShowtimeService{
    Page<ShowtimeResponse> getShowtimes(int page, int size);

    ShowtimeResponse getShowtimeById(Long id);

    UpdateShowtimeRequest getShowtimeForUpdate(Long id);

    ShowtimeResponse createShowtime(CreateShowtimeRequest request);

    ShowtimeResponse updateShowtime(Long id, UpdateShowtimeRequest request);

    void deleteShowtime(Long id);
}
