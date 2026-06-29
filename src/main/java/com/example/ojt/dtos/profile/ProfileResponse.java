package com.example.ojt.dtos.profile;

import com.example.ojt.roles.Gender;
import com.example.ojt.roles.Role;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileResponse {
    private Long id;

    private String username;

    private String email;

    private String fullName;

    private String phone;

    private Gender gender;

    private LocalDate birthday;

    private String address;

    private String avatarUrl;

    private Role role;

    private Boolean enabled;

    private LocalDateTime createdAt;
}
