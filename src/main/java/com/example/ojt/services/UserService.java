package com.example.ojt.services;

import com.example.ojt.entities.User;
import com.example.ojt.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository ur;
    public Optional<User> findByUsername(String username) {
        return ur.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return ur.findByEmail(email);
    }

    public User save(User user) {
        return ur.save(user);
    }
}
