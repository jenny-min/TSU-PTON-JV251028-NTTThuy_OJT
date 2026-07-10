package com.example.ojt.controllers;

import com.example.ojt.dtos.movie.CreateMovieRequest;
import com.example.ojt.dtos.movie.MovieResponse;
import com.example.ojt.dtos.movie.UpdateMovieRequest;
import com.example.ojt.enums.MovieStatus;
import com.example.ojt.services.interfaces.GenreService;
import com.example.ojt.services.interfaces.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/movies")
@RequiredArgsConstructor
public class MovieController {

    private final MovieService movieService;
    private final GenreService genreService;

    //Render danh sách phim
    @GetMapping
    public String getMovie(@RequestParam(defaultValue = "0") int page,
                           @RequestParam(defaultValue = "5") int size,
                           Model model) {

        Page<MovieResponse> moviePage =
                movieService.getMovies(page, size);

        model.addAttribute("movies", moviePage.getContent());
        model.addAttribute("pageData", moviePage);
        model.addAttribute("baseUrl", "/admin/movies");

        return "admin/movies/index";
    }

    //Load trang thêm
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("movie", new CreateMovieRequest());
        model.addAttribute("genres", genreService.findAll());
        model.addAttribute("movieStatuses", MovieStatus.values());
        return "admin/movies/create";
    }

    //Thêm phim
    @PostMapping("/create")
    public String saveCreate(
            @Valid
            @ModelAttribute("movie")
            CreateMovieRequest request,
            BindingResult result,
            Model model) {

        if (request.getGenreIds() == null || request.getGenreIds().isEmpty()) {

            result.rejectValue(
                    "genreIds",
                    "error.genreIds",
                    "Phim phải có ít nhất một thể loại");
        }

        if (result.hasErrors()) {
            model.addAttribute("genres", genreService.findAll());
            model.addAttribute("movieStatuses", MovieStatus.values());
            return "admin/movies/create";
        }

        movieService.addMovie(request);
        return "redirect:/admin/movies";
    }

    //Load trang edit
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        MovieResponse movie = movieService.getMovieById(id);
        model.addAttribute("movie", movieService.getMovieForEdit(id));

        model.addAttribute("genres", genreService.findAll());

        model.addAttribute("movieStatuses", MovieStatus.values());

        return "admin/movies/edit";
    }

    //Lưu edit
    @PostMapping("/edit/{id}")
    public String saveEdit(
            @PathVariable Long id,
            @Valid
            @ModelAttribute("movie")
            UpdateMovieRequest request,
            BindingResult result,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("genres", genreService.findAll());
            model.addAttribute("movieStatuses", MovieStatus.values());
            return "admin/movies/edit";
        }

        movieService.editMovie(id, request);

        return "redirect:/admin/movies";
    }

    //Xóa
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        System.out.println("DELETE MOVIE: " + id);

        movieService.deleteMovie(id);

        return "redirect:/admin/movies";
    }
}
