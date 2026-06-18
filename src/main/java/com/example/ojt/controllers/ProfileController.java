package com.example.ojt.controllers;

import com.example.ojt.entities.User;
import com.example.ojt.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class ProfileController {
    private final UserService us;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/profile")
    public String profile(Authentication authentication, Model model) {
        User user = us.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy"));

        model.addAttribute("user", user);
        return "profile/index";
    }

    @GetMapping("/profile/change-password")
    public String changePasswordPage(Authentication authentication, Model model) {

        User user = us.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy"));

        model.addAttribute("user", user);
        return "profile/change-password";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(
            @RequestParam String currentPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        User user = us.findByUsername(authentication.getName())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy user"));

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPassword())) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Mật khẩu hiện tại không đúng");

            return "redirect:/change-password";
        }

        if (!newPassword.equals(confirmPassword)) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    "Xác nhận mật khẩu không khớp");

            return "redirect:/change-password";
        }

        user.setPassword(
                passwordEncoder.encode(newPassword));

        us.save(user);

        redirectAttributes.addFlashAttribute(
                "success",
                "Đổi mật khẩu thành công");

        return "redirect:/profile";
    }
}
