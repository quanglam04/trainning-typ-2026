package com.example.coursemanagement_finalprojectmodule03.controller;

import com.example.coursemanagement_finalprojectmodule03.constant.Role;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.*;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.UserProfileResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.wrapper.ApiResponse;
import com.example.coursemanagement_finalprojectmodule03.model.service.IUserService;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    // API-4,31: Lấy danh sách tất cả người dùng (lọc theo role, trạng thái)
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserProfileResponse>>> getAllUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(required = false) Boolean status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name())
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<UserProfileResponse> data = userService.getAllUsers(role, status, pageable);

        return ResponseEntity.ok(ApiResponse.<Page<UserProfileResponse>>builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách tất cả người dùng thành công")
                .data(data)
                .build());
    }

    // API-5: Lấy thông tin chi tiết một người dùng
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{user_id}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getUserById(@PathVariable("user_id") Integer userId) {
        UserProfileResponse data = userService.getUserById(userId);
        return ResponseEntity.ok(ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .statusCode(200)
                .message("Lấy chi tiết người dùng thành công")
                .data(data)
                .build());
    }

    // API-6: Tạo tài khoản người dùng mới
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<UserProfileResponse>> createUser(@Valid @RequestBody UserCreateRequest request) {
        UserProfileResponse data = userService.createUser(request);
        return ResponseEntity.status(201).body(ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .statusCode(201)
                .message("Tạo tài khoản thành công")
                .data(data)
                .build());
    }

    // API-7: Cập nhật vai trò (role) của người dùng
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{user_id}/role")
    public ResponseEntity<ApiResponse<Void>> updateUserRole(
            @PathVariable("user_id") Integer targetUserId,
            @Valid @RequestBody UserUpdateRoleRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser
    ) {
        userService.updateUserRole(targetUserId, request, currentUser.getUser().getId());
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .statusCode(200)
                .message("Cập nhật quyền thành công")
                .build());
    }

    // API-8: Kích hoạt/vô hiệu hóa tài khoản người dùng
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{user_id}/status")
    public ResponseEntity<ApiResponse<Void>> updateUserStatus(
            @PathVariable("user_id") Integer userId,
            @Valid @RequestBody UserUpdateStatusRequest request
    ) {
        userService.updateUserStatus(userId, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .statusCode(200)
                .message("Cập nhật trạng thái thành công")
                .build());
    }

    // API-9: Xóa người dùng khỏi hệ thống
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{user_id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable("user_id") Integer userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .statusCode(200)
                .message("Xóa người dùng thành công")
                .build());
    }

    // API-26: Cập nhật thông tin cá nhân của người dùng
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.user.id")
    @PutMapping("/{user_id}")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            @PathVariable("user_id") Integer userId,
            @Valid @RequestBody UserUpdateRequest request
    ) {
        UserProfileResponse data = userService.updateProfile(userId, request);
        return ResponseEntity.ok(ApiResponse.<UserProfileResponse>builder()
                .success(true)
                .statusCode(200)
                .data(data)
                .message("Cập nhật hồ sơ thành công")
                .build());
    }

    // API-27: Đổi mật khẩu của người dùng
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.user.id")
    @PutMapping("/{user_id}/password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable("user_id") Integer userId,
            @Valid @RequestBody UserChangePasswordRequest request
    ) {
        userService.changePassword(userId, request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .statusCode(200)
                .message("Đổi mật khẩu thành công")
                .build());
    }
}