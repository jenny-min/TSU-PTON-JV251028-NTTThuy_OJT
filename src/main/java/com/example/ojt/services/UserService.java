package com.example.ojt.services;

import com.example.ojt.entities.User;
import java.util.Optional;

public interface UserService {
    void updateProfile(User user);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    User save(User user);
}
