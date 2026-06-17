package com.example.ojt.configs;

import com.example.ojt.entities.User;
import com.example.ojt.repositories.UserRepository;
import com.example.ojt.roles.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final UserRepository ur;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (ur.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("Administrator")
                    .email("admin@gmail.com")
                    .role(Role.ADMIN)
                    .build();

            ur.save(admin);

            User user = User.builder()
                    .username("user")
                    .password(passwordEncoder.encode("user123"))
                    .fullName("User")
                    .email("user@gmail.com")
                    .role(Role.USER)
                    .build();

            ur.save(user);

        }
    }
}
