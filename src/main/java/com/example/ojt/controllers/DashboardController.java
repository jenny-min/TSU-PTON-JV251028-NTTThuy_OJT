package com.example.ojt.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping
public class DashboardController {
    @GetMapping("/dashboard")
    public String dashboard(Authentication auth) {

        return auth.getAuthorities().stream()
                .map(a -> a.getAuthority())
                .findFirst()
                .map(role -> {
                    if (role.equals("ROLE_ADMIN")) return "redirect:/admin/dashboard";
                    if (role.equals("ROLE_STAFF")) return "redirect:/staff/dashboard";
                    return "redirect:/user/dashboard";
                })
                .orElse("redirect:/home");
    }
}
