package com.example.ojt.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class HomeController {
    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {

        model.addAttribute("username", authentication.getName());

        return "/home/dashboard";
    }
}
