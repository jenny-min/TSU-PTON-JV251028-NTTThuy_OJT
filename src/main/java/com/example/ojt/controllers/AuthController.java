package com.example.ojt.controllers;

import com.example.ojt.dtos.requests.RegisterRequest;
import com.example.ojt.repositories.UserRepository;
import com.example.ojt.roles.Gender;
import com.example.ojt.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping()
@RequiredArgsConstructor
public class AuthController {
    private final UserRepository ur;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;

    //Login
    @GetMapping("/login")
    public String loginPage() {
        return "/auth/login";
    }

    //Register
    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new RegisterRequest());
        model.addAttribute("genders", Gender.values());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("user") RegisterRequest request,
                           BindingResult result,
                           Model model) {
        model.addAttribute("genders", Gender.values());

        // VALIDATION ERROR (FIELD LEVEL)
        if (result.hasErrors()) {
            return "auth/register";
        }

        // PASSWORD CHECK
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            model.addAttribute("error", "Mật khẩu không trùng khớp");
            return "auth/register";
        }

        boolean success = authService.register(request);

        if (!success) {
            model.addAttribute("error", "Username hoặc Email đã tồn tại");
            return "auth/register";
        }

        return "redirect:/login";
    }
}
