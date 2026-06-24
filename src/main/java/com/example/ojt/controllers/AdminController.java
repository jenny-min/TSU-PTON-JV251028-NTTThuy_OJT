package com.example.ojt.controllers;

import com.example.ojt.entities.Movie;
import com.example.ojt.roles.MovieStatus;
import com.example.ojt.services.interfaces.GenreService;
import com.example.ojt.services.interfaces.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    private final MovieService movieService;
    private final GenreService genreService;

    //Vào dashboard admin
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }

    //Render danh sách phim
    @GetMapping("/movies")
    public String getMovie(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        Page<Movie> moviePage = movieService.getMovies(page, size);

        model.addAttribute("moviePage", moviePage);
        model.addAttribute("movies", moviePage.getContent());
        return "admin/movies/index";
    }

    //Load trang thêm
    @GetMapping("/movies/create")
    public String createForm(Model model) {
        model.addAttribute("movie", new Movie());
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("movieStatuses", MovieStatus.values());
        return "admin/movies/create";
    }

    //Thêm phim
    @PostMapping("/movies/create")
    public String saveCreate(
            @Valid @ModelAttribute("movie") Movie movie,
            BindingResult result,
            @RequestParam(required = false) List<Long> genreIds,
            Model model) {

        if (genreIds == null || genreIds.isEmpty()) {
            result.rejectValue("genres", "error.genres",
                    "Phim phải có ít nhất một thể loại");
        }

        if (result.hasErrors()) {
            model.addAttribute("genres", genreService.findAll());
            model.addAttribute("movieStatuses", MovieStatus.values());
            return "admin/movies/create";
        }

        movieService.addMovie(movie, genreIds);
        return "redirect:/admin/movies";
    }

    //Load trang edit
    @GetMapping("/movies/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Movie movie = movieService.getMovieById(id);
        model.addAttribute("movie", movie);
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("movieStatuses", MovieStatus.values());
        return "admin/movies/edit";
    }

    //Lưu edit
    @PostMapping("/movies/edit/{id}")
    public String saveEdit(
            @PathVariable Long id,
            @Valid @ModelAttribute("movie") Movie movie,
            BindingResult result,
            @RequestParam(required = false) List<Long> genreIds,
            Model model
    ) {

        if (result.hasErrors()) {
            model.addAttribute("genres", genreService.findAll());
            model.addAttribute("movieStatuses", MovieStatus.values());
            return "admin/movies/edit";
        }

        movieService.editMovie(id, movie, genreIds);

        return "redirect:/admin/movies";
    }

    //Xóa
    @PostMapping("/movies/delete/{id}")
    public String delete(@PathVariable Long id) {
        System.out.println("DELETE MOVIE: " + id);

        movieService.deleteMovie(id);

        return "redirect:/admin/movies";
    }
}
