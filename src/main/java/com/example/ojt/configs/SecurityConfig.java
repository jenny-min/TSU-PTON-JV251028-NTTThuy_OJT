package com.example.ojt.configs;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CustomAuthenticationSuccessHandler successHandler;

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(auth -> auth
                        // public
                        .requestMatchers("/login", "/register", "/css/**", "/home", "/").permitAll()

                        //profile
                        .requestMatchers("/profile/**").hasAnyRole("USER","STAFF","ADMIN")

                        // STAFF area (rạp phim)
                        .requestMatchers("/staff/**").hasAnyRole("STAFF", "ADMIN")

                        // ADMIN
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        //USER
                        .requestMatchers("/user/**").hasAnyRole("USER", "STAFF", "ADMIN")

                        // còn lại phải login
                        .anyRequest().authenticated()
                )

                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler)
                        .failureUrl("/login?error")
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                );

        return http.build();
    }
}
