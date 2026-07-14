package com.example.ojt.controllers;

import com.example.ojt.dtos.profile.ChangePasswordRequest;
import com.example.ojt.dtos.profile.ProfileResponse;
import com.example.ojt.dtos.profile.UpdateProfileRequest;
import com.example.ojt.entities.User;
import com.example.ojt.enums.Gender;
import com.example.ojt.services.CustomOAuth2User;
import com.example.ojt.services.CustomUserDetails;
import com.example.ojt.services.interfaces.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class ProfileController {

    private final UserService us;

    @GetMapping("/profile")
    public String profile(
            Authentication authentication,
            Model model
    ) {
        ProfileResponse profile = us.getProfile(authentication.getName());

        UpdateProfileRequest profileForm =
                UpdateProfileRequest.builder()
                        .fullName(profile.getFullName())
                        .phone(profile.getPhone())
                        .gender(profile.getGender())
                        .birthday(profile.getBirthday())
                        .address(profile.getAddress())
                        .avatarUrl(profile.getAvatarUrl())
                        .build();

        model.addAttribute("profile", profile);
        model.addAttribute("profileForm", profileForm);
        model.addAttribute("genders", Gender.values());

        return "profile/index";
    }

    @GetMapping("/profile/change-password")
    public String changePasswordPage(Model model) {

        model.addAttribute("changePasswordRequest", new ChangePasswordRequest());

        return "profile/change-password";
    }

    @PostMapping("/profile/change-password")
    public String changePassword(
            @Valid
            @ModelAttribute("changePasswordRequest")
            ChangePasswordRequest request,
            BindingResult result,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {

        if (result.hasErrors()) {
            return "profile/change-password";
        }

        try {
            us.changePassword(authentication.getName(), request);

            redirectAttributes.addFlashAttribute("success",
                    "Đổi mật khẩu thành công"
            );

        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(
            Authentication authentication,
            @Valid
            @ModelAttribute("profile")
            UpdateProfileRequest request,
            BindingResult result,
            @RequestParam("avatarFile")
            MultipartFile file
    ) throws IOException {
        if (result.hasErrors()) {
            return "profile/index";
        }

        // Dùng username (getName) để form login + Google login đều chạy
        User currentUser = us.findByUsername(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        us.updateProfile(currentUser.getId(), request, file);

        User updatedUser = us.findById(currentUser.getId());
        refreshPrincipal(authentication, updatedUser);

        return "redirect:/profile";
    }

    /**
     * Cập nhật principal trong session sau khi sửa profile
     * (giữ đúng loại: form login = CustomUserDetails, Google = CustomOAuth2User).
     */
    private void refreshPrincipal(Authentication authentication, User updatedUser) {
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomOAuth2User oauth2User) {
            CustomOAuth2User newPrincipal =
                    new CustomOAuth2User(oauth2User.getOauth2User(), updatedUser);
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    newPrincipal,
                    authentication.getCredentials(),
                    newPrincipal.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(auth);
            return;
        }

        CustomUserDetails newPrincipal = new CustomUserDetails(updatedUser);
        Authentication auth = new UsernamePasswordAuthenticationToken(
                newPrincipal,
                null,
                newPrincipal.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }
}
