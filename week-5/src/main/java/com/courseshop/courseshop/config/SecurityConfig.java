package com.courseshop.courseshop.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.courseshop.courseshop.dto.RestErrorDTO;
import com.courseshop.courseshop.repository.UserRepository;
import com.courseshop.courseshop.security.JwtAuthenticationFilter;
import com.courseshop.courseshop.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    // Hash mật khẩu mới bằng BCrypt; xác minh hash Argon2 cũ để nâng cấp khi đăng nhập.
    @Bean
    PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        PasswordEncoder bcrypt = new BCryptPasswordEncoder(12);
        encoders.put("bcrypt", bcrypt);
        encoders.put("argon2id", Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8());
        DelegatingPasswordEncoder encoder = new DelegatingPasswordEncoder("bcrypt", encoders);
        encoder.setDefaultPasswordEncoderForMatches(bcrypt);
        return encoder;
    }

    /** Cấu hình provider dùng AppUserDetailsService và PasswordEncoder để kiểm tra username/password. */
    @Bean
    AuthenticationProvider authenticationProvider(
            com.courseshop.courseshop.service.AppUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /** Cung cấp AuthenticationManager để AuthService có thể chạy quy trình xác thực chuẩn của Spring Security. */
    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    // Cấu hình JWT và phân quyền: public gồm /, /error, /auth, POST /users; ADMIN quản lý /admin và /users; USER/ADMIN dùng /todos.
    @Bean
    SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            AuthService authService,
            UserRepository userRepository,
            ObjectMapper objectMapper) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .httpBasic(basic -> basic.disable())
            .formLogin(form -> form.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint((request, response, exception) -> writeSecurityError(
                        objectMapper, response, HttpServletResponse.SC_UNAUTHORIZED,
                        "Unauthorized", "Cần đăng nhập bằng Bearer token"))
                .accessDeniedHandler((request, response, exception) -> {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
                    response.setContentType("text/plain;charset=UTF-8");
                    response.getWriter().write("User không được phép truy cập");
                }))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/", "/login.html", "/error", "/auth/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/users").permitAll()
                .requestMatchers("/admin/**", "/users", "/users/**", "/users-all").hasRole("ADMIN")
                .requestMatchers("/todos/**", "/todos-all").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated())
            .addFilterBefore(
                    new JwtAuthenticationFilter(authService, userRepository, objectMapper),
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private static void writeSecurityError(
            ObjectMapper objectMapper,
            HttpServletResponse response,
            int status,
            String error,
            String message) throws java.io.IOException {
        response.setStatus(status);
        response.setHeader(HttpHeaders.CACHE_CONTROL, "no-store");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(), new RestErrorDTO(status, error, message));
    }
}
