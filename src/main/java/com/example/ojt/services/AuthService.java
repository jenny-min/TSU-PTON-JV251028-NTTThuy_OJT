package com.example.ojt.services;

import com.example.ojt.dtos.auth.RegisterRequest;
import com.example.ojt.entities.User;
import com.example.ojt.repositories.UserRepository;
import com.example.ojt.enums.Role;
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
        user.setGender(request.getGender());
        user.setBirthday(request.getBirthday());
        user.setAddress(request.getAddress());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        ur.save(user);

        return true;
    }
}
