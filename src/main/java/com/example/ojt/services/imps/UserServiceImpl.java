package com.example.ojt.services.imps;

import com.example.ojt.entities.User;
import com.example.ojt.repositories.UserRepository;
import com.example.ojt.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository ur;
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
