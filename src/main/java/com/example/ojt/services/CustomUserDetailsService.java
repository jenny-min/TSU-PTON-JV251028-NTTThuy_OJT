package com.example.ojt.services;

import com.example.ojt.entities.User;
import com.example.ojt.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UserRepository ur;

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        User user = ur.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("Không tìm thấy người dùng"));

        return new CustomUserDetails(user);
    }
}
