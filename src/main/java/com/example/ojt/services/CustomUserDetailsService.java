package com.example.ojt.services;

import com.example.ojt.entities.User;
import com.example.ojt.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository ur;

    @Override
    public UserDetails loadUserByUsername(String username) {

        Optional<User> user = ur.findByUsername(username);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("Không tìm thấy tên người dùng" + username);
        }

        return new org.springframework.security.core.userdetails.User(
                user.get().getUsername(),
                user.get().getPassword(),
                new ArrayList<>() // chưa phân quyền
        );
    }
}
