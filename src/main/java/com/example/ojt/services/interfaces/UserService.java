package com.example.ojt.services.interfaces;

import com.example.ojt.dtos.profile.ChangePasswordRequest;
import com.example.ojt.dtos.profile.ProfileResponse;
import com.example.ojt.dtos.profile.UpdateProfileRequest;
import com.example.ojt.entities.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

public interface UserService {
    void updateProfile(Long userId, UpdateProfileRequest request, MultipartFile file ) throws IOException;
    void changePassword( String username, ChangePasswordRequest request );
    ProfileResponse getProfile(String username);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    User findById(Long id);
}
