package com.example.ojt.services.imps;

import com.example.ojt.entities.User;
import com.example.ojt.repositories.UserRepository;
import com.example.ojt.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository ur;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void updateProfile(User userForm, MultipartFile file) throws IOException {
        User userDb = ur.findById(userForm.getId())
                .orElseThrow(() ->
                        new RuntimeException(
                                "Không tìm thấy người dùng"));

        userDb.setFullName(userForm.getFullName());
        userDb.setPhone(userForm.getPhone());
        userDb.setAddress(userForm.getAddress());
        userDb.setGender(userForm.getGender());
        userDb.setBirthday(userForm.getBirthday());

        if (!file.isEmpty()) {

            List<String> allowedTypes = List.of(
                    "image/jpeg",
                    "image/png",
                    "image/gif",
                    "image/webp"
            );

            if (!allowedTypes.contains(file.getContentType())) {
                throw new RuntimeException(
                        "File không phải ảnh");
            }

            if (file.getSize() > 5 * 1024 * 1024) {
                throw new RuntimeException(
                        "Ảnh vượt quá 5MB");
            }

            if (userDb.getAvatarUrl() != null
                    && userDb.getAvatarUrl()
                    .startsWith("/uploads/avatars/")) {

                String oldFile =
                        userDb.getAvatarUrl()
                                .replace(
                                        "/uploads/avatars/",
                                        "");

                Files.deleteIfExists(
                        Paths.get("uploads/avatars")
                                .resolve(oldFile));
            }

            String fileName =
                    UUID.randomUUID()
                            + "_"
                            + file.getOriginalFilename();

            Path uploadPath =
                    Paths.get("uploads/avatars");

            Files.createDirectories(uploadPath);

            file.transferTo(
                    uploadPath.resolve(fileName));

            userDb.setAvatarUrl(
                    "/uploads/avatars/" + fileName);
        }

        ur.save(userDb);
    }

    @Override
    public void changePassword(
            String username, String currentPassword,
            String newPassword, String confirmPassword) {
        User user = ur.findByUsername(username)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy user"));

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPassword())) {

            throw new RuntimeException(
                    "Mật khẩu hiện tại không đúng");
        }

        if (!newPassword.equals(confirmPassword)) {

            throw new RuntimeException(
                    "Xác nhận mật khẩu không khớp");
        }

        user.setPassword(
                passwordEncoder.encode(newPassword));

        ur.save(user);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return ur.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return ur.findByEmail(email);
    }

    @Override
    public User save(User user) {
        return ur.save(user);
    }

    @Override
    public User findById(Long id) {
        return ur.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy người dùng"));
    }

    @Override
    public void updateProfile(User userForm) {

        User userDb = ur.findById(userForm.getId())
                .orElseThrow(() ->
                        new RuntimeException("Không tìm thấy người dùng"));

        userDb.setFullName(userForm.getFullName());
        userDb.setPhone(userForm.getPhone());
        userDb.setAddress(userForm.getAddress());
        userDb.setGender(userForm.getGender());
        userDb.setBirthday(userForm.getBirthday());

        if (userForm.getAvatarUrl() != null
                && !userForm.getAvatarUrl().isBlank()) {
            userDb.setAvatarUrl(userForm.getAvatarUrl());
        }

        ur.save(userDb);
    }
}
