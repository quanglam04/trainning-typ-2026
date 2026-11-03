package com.example.coursemanagement_finalprojectmodule03.controller;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.*;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.AuthLoginResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.AuthProfileResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.AuthRefreshAccessTokenResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.wrapper.ApiResponse;
import com.example.coursemanagement_finalprojectmodule03.model.service.IAuthService;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/auth")
public class AuthController {
    private final IAuthService authService;


    // API-01_Phu: Đăng ký tài khoản (PUBLIC)
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody AuthRegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(201).body(
                ApiResponse.builder()
                        .success(true)
                        .statusCode(201)
                        .message("Đăng ký thành công! Vui lòng kiểm tra email để lấy mã OTP kích hoạt.")
                        .build()
        );
    }

    // API-02_Phu: Kích hoạt tài khoản bằng OTP (PUBLIC)
    @PostMapping("/active")
    public ResponseEntity<?> activeUser(@Valid @RequestBody AuthActiveRequest request) {
        String message = authService.activeUser(request);
        return ResponseEntity.status(200).body(
                ApiResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message(message)
                        .build()
        );
    }

    // API-1: Đăng nhập và nhận token xác thực JWT
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthLoginRequest request) {
        AuthLoginResponse response = authService.login(request);
        return ResponseEntity.status(200).body(
                ApiResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Đăng nhập thành công!")
                        .data(response)
                        .build()
        );
    }

    // API-2: Xác thực token người dùng
    @PostMapping("/verify")
    public ResponseEntity<ApiResponse<Void>> verifyToken(@AuthenticationPrincipal CustomUserDetails currentUser) {
        authService.verifyToken(currentUser);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .statusCode(200)
                .message("Token hợp lệ!")
                .build());
    }

    // API-03_Phu: Refresh Token để lấy Access Token mới (PUBLIC)
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody AuthRefreshTokenRequest request) {
        AuthRefreshAccessTokenResponse response = authService.refreshToken(request);
        return ResponseEntity.status(200).body(
                ApiResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Cấp lại Access Token thành công!")
                        .data(response)
                        .build()
        );
    }

    // API-3: Lấy thông tin hồ sơ của người dùng hiện tại
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {
        AuthProfileResponse profile = authService.getMe(userDetails);
        return ResponseEntity.status(200).body(
                ApiResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Lấy thông tin tài khoản thành công.")
                        .data(profile)
                        .build()
        );
    }

    // API-04_Phu: Quên mật khẩu - gửi OTP (PUBLIC)
    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@Valid @RequestBody AuthForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.status(200).body(
                ApiResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Mã OTP đặt lại mật khẩu đã được gửi đến email của bạn.")
                        .build()
        );
    }

    // API-05_Phu: Đặt lại mật khẩu mới (PUBLIC)
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody AuthResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.status(200).body(
                ApiResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Đặt lại mật khẩu thành công! Bạn có thể đăng nhập bằng mật khẩu mới.")
                        .build()
        );
    }

    // API-30: Đăng xuất tài khoản
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            @Valid @RequestBody AuthLogoutRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {
        authService.logout(authHeader, request, currentUser);
        return ResponseEntity.status(200).body(
                ApiResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Đăng xuất thành công!")
                        .build()
        );
    }
}
