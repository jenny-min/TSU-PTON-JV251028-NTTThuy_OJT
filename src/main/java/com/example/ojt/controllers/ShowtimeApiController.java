package com.example.ojt.controllers;

import com.example.ojt.dtos.showtime.ShowtimeResponse;
import com.example.ojt.services.interfaces.ShowtimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class ShowtimeApiController {
    private final ShowtimeService showtimeService;

    @GetMapping("/showtimes")
    @ResponseBody
    public List<ShowtimeResponse> getShowtimesByMovie(@RequestParam("movieId") Long movieId) {
        return showtimeService.getUpcomingShowtimesByMovieId(movieId);
    }
}
