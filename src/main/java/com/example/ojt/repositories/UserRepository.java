package com.example.ojt.repositories;

import com.example.ojt.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    // Đếm số lượng khách hàng đang hoạt động hoặc bị khóa
    long countByEnabled(Boolean enabled);
}
