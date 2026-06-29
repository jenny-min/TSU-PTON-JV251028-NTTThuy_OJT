package com.example.ojt.controllers;

import com.example.ojt.dtos.auth.LoginRequest;
import com.example.ojt.dtos.auth.RegisterRequest;
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
    private final AuthService authService;

    //Login
    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("loginRequest", new LoginRequest());
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

        //Validation
        if (result.hasErrors()) {
            return "auth/register";
        }

        try {
            authService.register(request);
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());

            return "auth/register";
        }
    }
}
