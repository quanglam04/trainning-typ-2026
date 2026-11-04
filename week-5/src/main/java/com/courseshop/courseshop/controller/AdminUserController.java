package com.courseshop.courseshop.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.courseshop.courseshop.dto.UpdateUserRoleRequest;
import com.courseshop.courseshop.model.User;
import com.courseshop.courseshop.service.UserService;

@RestController
@RequestMapping("/admin/users")
// Chặn toàn bộ endpoint trong controller này; chỉ tài khoản có role ADMIN được gọi.
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    public AdminUserController(UserService userService) {
        this.userService = userService;
    }

    // GET /admin/users: admin xem danh sách tất cả tài khoản.
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsersForAdmin());
    }

    // PATCH /admin/users/{id}/role: admin đổi role của một tài khoản.
    @PatchMapping("/{id}/role")
    public ResponseEntity<User> updateUserRole(
            @PathVariable Long id,
            @RequestBody UpdateUserRoleRequest request) {
        return ResponseEntity.ok(userService.updateUserRole(id, request.getRole()));
    }

    // DELETE /admin/users/{id}: admin xóa một tài khoản theo ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUserForAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
