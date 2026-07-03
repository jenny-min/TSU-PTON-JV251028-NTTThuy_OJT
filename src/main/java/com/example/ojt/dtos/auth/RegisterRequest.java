package com.example.ojt.dtos.auth;

import com.example.ojt.enums.Gender;
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
    @Email(message = "Email không hợp lệ")
    private String email;

    @NotBlank(message = "Mật khẩu không được trống")
    @Size(min = 6, message = "Mật khẩu phải có ít nhất 6 ký tự")
    private String password;

    @NotBlank(message = "Xác nhận mật khẩu không được trống")
    private String confirmPassword;

    private String fullName;

    private String phone;

    private Gender gender;

    private LocalDate birthday;

    private String address;
}
