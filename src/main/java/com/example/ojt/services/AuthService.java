package com.example.ojt.services;

import com.example.ojt.dtos.requests.RegisterRequest;
import com.example.ojt.entities.User;
import com.example.ojt.repositories.UserRepository;
import com.example.ojt.roles.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository ur;
    private final PasswordEncoder passwordEncoder;

    public boolean register(RegisterRequest request) {

        if (ur.existsByUsername(request.getUsername())) return false;

        if (ur.existsByEmail(request.getEmail())) return false;

        if (!request.getPassword().equals(request.getConfirmPassword())) return false;

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());

        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        ur.save(user);

        return true;
    }

    public boolean login(String username, String password) {

        User user = ur.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Sai mật khẩu hoặc tên đăng nhập"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return false;
        }

        return true;
    }
}
