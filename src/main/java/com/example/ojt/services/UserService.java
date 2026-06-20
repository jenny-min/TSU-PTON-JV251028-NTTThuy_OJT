package com.example.ojt.services;

import com.example.ojt.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface UserService {
    void updateProfile(User userForm, MultipartFile file) throws IOException;

    void changePassword(
            String username,
            String currentPassword,
            String newPassword,
            String confirmPassword
    );

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User save(User user);

    User findById(Long id);

    void updateProfile(User userForm);
}
