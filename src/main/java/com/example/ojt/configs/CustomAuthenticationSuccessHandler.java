package com.example.ojt.configs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException {

        String redirectUrl = "/";

        if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {

            redirectUrl = "/admin/dashboard";

        } else if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STAFF"))) {

            redirectUrl = "/staff/dashboard";

        } else if (authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER"))){

            redirectUrl = "/home";

        } else {
            redirectUrl = "/home";
        }

        response.sendRedirect(redirectUrl);
    }
}
