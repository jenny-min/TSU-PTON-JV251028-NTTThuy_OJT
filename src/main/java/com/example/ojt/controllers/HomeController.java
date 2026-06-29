package com.example.ojt.controllers;

import com.example.ojt.dtos.movie.MovieResponse;
import com.example.ojt.services.interfaces.MovieService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping()
@RequiredArgsConstructor
public class HomeController {
    private final MovieService movieService;

    @GetMapping("/home")
    public String home(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "8") int size,
                       Authentication authentication,
                       Model model) {

        if (authentication != null) {
            model.addAttribute("username", authentication.getName());
        }

        Page<MovieResponse> moviePage = movieService.getMovies(page, size);

        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("pageData", moviePage);
        model.addAttribute("baseUrl", "/home");

        return "home/index";
    }
}
