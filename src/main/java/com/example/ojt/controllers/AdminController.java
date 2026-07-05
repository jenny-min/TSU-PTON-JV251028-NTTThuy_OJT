package com.example.ojt.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
    //Vào dashboard admin
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/dashboard";
    }
}
