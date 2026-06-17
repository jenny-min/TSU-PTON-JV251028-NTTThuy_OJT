package com.example.ojt.dtos.requests;

import com.example.ojt.roles.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank(message = "Tên người dùng không được trống")
    @Size(min = 3, message = "Tên người dùng không dưới 3 ký tự")
    private String username;

    @NotBlank(message = "Email không được trống")
    @Email
    private String email;

    @NotBlank(message = "Mật khẩu không được trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được trống")
    private String confirmPassword;

    @Column(name = "full_name", length = 100)
    private String fullName;

    @Column(length = 20)
    private String phone;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthday;

    @Column(length = 255)
    private String address;
}
