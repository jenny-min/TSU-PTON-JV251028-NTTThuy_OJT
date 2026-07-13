package com.example.ojt.controllers;

import com.example.ojt.dtos.showtime.CreateShowtimeRequest;
import com.example.ojt.dtos.showtime.ShowtimeResponse;
import com.example.ojt.dtos.showtime.UpdateShowtimeRequest;
import com.example.ojt.services.interfaces.MovieService;
import com.example.ojt.services.interfaces.RoomService;
import com.example.ojt.services.interfaces.ShowtimeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/admin/showtimes")
@RequiredArgsConstructor
public class ShowtimeController {
    private final ShowtimeService showtimeService;
    private final MovieService movieService;
    private final RoomService roomService;

    //Danh sách suất chiếu
    @GetMapping
    public String showtimePage(
            @RequestParam(value = "movieId", required = false) Long movieId,
            @RequestParam(value = "roomId", required = false) Long roomId,
            @RequestParam(value = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            Model model) {

        //Lấy dữ liệu phân trang kết hợp các tham số bộ lọc
        Page<ShowtimeResponse> showtimePage = showtimeService.getShowtimes(movieId, roomId, date, page, size);

        //Đổ dữ liệu phân trang ra View
        model.addAttribute("showtimePage", showtimePage);
        model.addAttribute("showtimes", showtimePage.getContent());
        model.addAttribute("pageData", showtimePage);
        model.addAttribute("baseUrl", "/admin/showtimes");

        //Đổ danh sách Phim và Phòng để hiển thị lên 2 ô select của bộ lọc
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("rooms", roomService.getAllRooms());

        return "admin/showtimes/index";
    }

    //Hiển thị form tạo
    @GetMapping("/create")
    public String createForm(Model model) {
        model.addAttribute("showtime", new CreateShowtimeRequest());
        model.addAttribute("movies", movieService.getAllMovies());
        model.addAttribute("rooms", roomService.getAllRooms());
        return "admin/showtimes/create";
    }

    //Lưu
    @PostMapping("/create")
    public String create(
            @Valid @ModelAttribute("showtime") CreateShowtimeRequest request,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("movies", movieService.getAllMovies());
            model.addAttribute("rooms", roomService.getAllRooms());
            return "admin/showtimes/create";
        }
        showtimeService.createShowtime(request);
        return "redirect:/admin/showtimes";
    }

    //Form cập nhật
    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable Long id, Model model,
                           RedirectAttributes redirectAttributes) {

        try {
            UpdateShowtimeRequest request = showtimeService.getShowtimeForUpdate(id);

            model.addAttribute("showtimes", request);
            model.addAttribute("movies", movieService.getAllMovies());
            model.addAttribute("rooms", roomService.getAllRooms());
            model.addAttribute("showtimeId", id);

            return "admin/showtimes/update";

        } catch (IllegalStateException ex) {

            redirectAttributes.addFlashAttribute("error", ex.getMessage());

            return "redirect:/admin/showtimes";
        }
    }

    @PostMapping("/edit/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("showtimes") UpdateShowtimeRequest request,
            BindingResult result,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("showtimeId", id);
            model.addAttribute("movies", movieService.getAllMovies());
            model.addAttribute("rooms", roomService.getAllRooms());
            return "admin/showtimes/update";
        }

        showtimeService.updateShowtime(id, request);

        return "redirect:/admin/showtimes";
    }

    //Xóa
    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id,
                         RedirectAttributes redirectAttributes) {
        try {
            showtimeService.deleteShowtime(id);

            redirectAttributes.addFlashAttribute("success",
                    "Xóa suất chiếu thành công.");

        } catch (IllegalStateException ex) {

            redirectAttributes.addFlashAttribute("error",
                    ex.getMessage());
        }

        return "redirect:/admin/showtimes";
    }

    @PostMapping("/{id}/publish")
    public String publish(@PathVariable("id") Long id) {
        showtimeService.publish(id);
        return "redirect:/admin/showtimes";
    }
}
