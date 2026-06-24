package com.example.ojt.controllers;

import com.example.ojt.entities.User;
import com.example.ojt.roles.Gender;
import com.example.ojt.services.CustomUserDetails;
import com.example.ojt.services.interfaces.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

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
        model.addAttribute("genders", Gender.values());
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

        try {
            us.changePassword(
                    authentication.getName(),
                    currentPassword,
                    newPassword,
                    confirmPassword
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Đổi mật khẩu thành công");

        } catch (RuntimeException e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage());
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            @AuthenticationPrincipal
            CustomUserDetails principal,
            @ModelAttribute User user,
            @RequestParam("avatarFile")
            MultipartFile file
    ) throws IOException {

        user.setId(
                principal.getUser().getId());

        us.updateProfile(user, file);

        User updatedUser =
                us.findById(user.getId());

        CustomUserDetails newPrincipal =
                new CustomUserDetails(updatedUser);

        Authentication auth =
                new UsernamePasswordAuthenticationToken(
                        newPrincipal,
                        null,
                        newPrincipal.getAuthorities()
                );

        SecurityContextHolder
                .getContext()
                .setAuthentication(auth);

        return "redirect:/dashboard";
    }
}
