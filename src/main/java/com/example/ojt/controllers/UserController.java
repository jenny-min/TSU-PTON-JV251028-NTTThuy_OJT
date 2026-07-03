package com.example.ojt.controllers;

import com.example.ojt.entities.User;
import com.example.ojt.services.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/dashboard")
    public String myPage(Authentication authentication, Model model) {

        String username = authentication.getName();

        User user = userService.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        model.addAttribute("user", user);
        model.addAttribute("totalTickets", 0);
        model.addAttribute("upcomingTickets", 0);
        model.addAttribute("watchedTickets", 0);

        return "user/dashboard";
    }
}
