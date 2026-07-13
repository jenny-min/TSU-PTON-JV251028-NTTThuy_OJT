package com.example.ojt.services;

import com.example.ojt.entities.User;
import com.example.ojt.enums.Role;
import com.example.ojt.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oauthUser = super.loadUser(userRequest);

        String email = oauthUser.getAttribute("email");
        String name = oauthUser.getAttribute("name");
        String picture = oauthUser.getAttribute("picture");

        User user = userRepository.findByEmail(email)
                .orElseGet(() -> {

                    User newUser = new User();

                    newUser.setEmail(email);
                    newUser.setFullName(name);
                    newUser.setAvatarUrl(picture);
                    newUser.setCreatedAt(LocalDateTime.now());
                    newUser.setUpdatedAt(LocalDateTime.now());

                    // username unique
                    newUser.setUsername(generateUsername(email));

                    // Google login không dùng password
                    newUser.setPassword(UUID.randomUUID().toString());

                    newUser.setRole(Role.USER);
                    newUser.setEnabled(true);

                    return userRepository.save(newUser);
                });

        return new CustomOAuth2User(
                oauthUser,
                user
        );
    }

    private String generateUsername(String email) {
        String username = email.split("@")[0];

        int count = 1;

        while (userRepository.findByUsername(username).isPresent()) {
            username = email.split("@")[0] + count++;
        }

        return username;
    }
}
