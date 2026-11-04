package com.courseshop.courseshop.security;

import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.courseshop.courseshop.dto.RestErrorDTO;
import com.courseshop.courseshop.model.User;
import com.courseshop.courseshop.model.UserRole;
import com.courseshop.courseshop.repository.UserRepository;
import com.courseshop.courseshop.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final AuthService authService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    public JwtAuthenticationFilter(
            AuthService authService,
            UserRepository userRepository,
            ObjectMapper objectMapper) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    // Xác định route đăng nhập/đăng ký công khai được bỏ qua bước xác thực JWT.
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return request.getServletPath().startsWith("/auth/")
                || ("POST".equalsIgnoreCase(request.getMethod())
                        && "/users".equals(request.getServletPath()));
    }

    // Xác thực Bearer JWT và gắn tài khoản cùng role vào request để Spring kiểm tra quyền.
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization.substring("Bearer ".length()).trim();
        try {
            Claims claims = authService.getClaimsFromValidToken(token);
            String username = claims.getSubject();
            if (username == null || username.isBlank()) {
                writeUnauthorized(response);
                return;
            }

            UserRole role = userRepository.findByEmail(username)
                    .map(User::getRole)
                    .orElseGet(() -> authService.isDemoUsername(username) ? UserRole.USER : null);
            if (role == null) {
                writeUnauthorized(response);
                return;
            }

            var authentication = UsernamePasswordAuthenticationToken.authenticated(
                    username,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role.name())));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request, response);
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            writeUnauthorized(response);
        }
    }

    // Trả response 401 dạng JSON khi Bearer token không hợp lệ hoặc không xác định được tài khoản.
    private void writeUnauthorized(HttpServletResponse response) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        objectMapper.writeValue(response.getOutputStream(),
                new RestErrorDTO(401, "Unauthorized", "Bearer token không hợp lệ hoặc đã hết hạn"));
    }
}
