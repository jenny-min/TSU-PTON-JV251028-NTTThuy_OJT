package com.example.ojt.dtos.profile;

import com.example.ojt.roles.Gender;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProfileRequest {
    @Size(max = 100, message = "Họ tên tối đa 100 ký tự")
    private String fullName;

    @Size(max = 20, message = "Số điện thoại tối đa 20 ký tự")
    private String phone;

    private Gender gender;

    private LocalDate birthday;

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String address;

    private String avatarUrl;
}
