package com.courseshop.courseshop.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.courseshop.courseshop.dto.LoginRequestDTO;
import com.courseshop.courseshop.service.AuthService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // POST /auth/session/login: kiểm tra thông tin đăng nhập và tạo session (cookie JSESSIONID).
    @PostMapping("/session/login")
    public ResponseEntity<?> loginWithSession(
            @RequestBody LoginRequestDTO loginRequest,
            HttpServletRequest request) {
        authService.loginWithSession(loginRequest, request);
        return ResponseEntity.ok(Map.of("message", "Đăng nhập session thành công"));
    }

    // GET /auth/session/me: trả về tài khoản gắn với session hiện tại.
    @GetMapping("/session/me")
    public ResponseEntity<?> getSessionUser(HttpServletRequest request) {
        return ResponseEntity.ok(Map.of("username", authService.getSessionUsername(request)));
    }

    // POST /auth/session/logout: hủy session hiện tại để đăng xuất.
    @PostMapping("/session/logout")
    public ResponseEntity<Void> logoutSession(HttpServletRequest request) {
        authService.logoutSession(request);
        return ResponseEntity.noContent().build();
    }

    // POST /auth/token/login: xác thực tài khoản và trả JWT để client gửi ở các request sau.
    @PostMapping("/token/login")
    public ResponseEntity<?> loginWithToken(@RequestBody LoginRequestDTO loginRequest) {
        return ResponseEntity.ok(authService.loginWithToken(loginRequest));
    }

    // POST /auth/token/refresh: thu hồi refresh token cũ và cấp cặp token mới.
    @PostMapping("/token/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody Map<String, String> body) {
        String oldRefreshToken = body == null ? null : body.get("refreshToken");
        return ResponseEntity.ok(authService.refreshTokens(oldRefreshToken));
    }

    // POST /auth/token/logout: thu hồi refresh token; access token hiện tại còn hiệu lực đến khi hết hạn.
    @PostMapping("/token/logout")
    public ResponseEntity<Void> logoutToken(@RequestBody Map<String, String> body) {
        authService.logoutToken(body == null ? null : body.get("refreshToken"));
        return ResponseEntity.noContent().build();
    }

    // GET /auth/token/me: kiểm tra Bearer token rồi trả về username trong token.
    @GetMapping("/token/me")
    public ResponseEntity<?> getTokenUser(
            @RequestHeader(name = "Authorization", required = false) String authorization) {
        return ResponseEntity.ok(Map.of("username", authService.getUsernameFromBearer(authorization)));
    }
}
