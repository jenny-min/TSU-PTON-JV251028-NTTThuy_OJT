package com.example.ojt.entities;

import com.example.ojt.roles.Gender;
import com.example.ojt.roles.Role;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthday;

    @Column(length = 255)
    private String address;

    @Column(name = "avatar_url", length = 255)
    private String avatarUrl;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    private Boolean enabled = true;

    @Column(name = "disable_reason", length = 255)
    private String disableReason;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user")
    private List<Booking> bookings;
}
