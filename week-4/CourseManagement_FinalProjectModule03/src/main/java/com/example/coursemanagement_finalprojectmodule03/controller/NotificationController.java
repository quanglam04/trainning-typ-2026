package com.example.coursemanagement_finalprojectmodule03.controller;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.NotificationCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.NotificationResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.wrapper.ApiResponse;
import com.example.coursemanagement_finalprojectmodule03.model.service.INotificationService;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final INotificationService notificationService;

    // API-33: Lấy danh sách thông báo của người dùng
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getMyNotifications(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        Page<NotificationResponse> data = notificationService.getMyNotifications(currentUser, page, size);
        return ResponseEntity.ok(ApiResponse.<Page<NotificationResponse>>builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách thông báo thành công")
                .data(data)
                .build());
    }

    // API-34: Đánh dấu thông báo đã đọc
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{notification_id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @PathVariable("notification_id") Integer notificationId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        NotificationResponse data = notificationService.markAsRead(notificationId, currentUser);
        return ResponseEntity.ok(ApiResponse.<NotificationResponse>builder()
                .success(true)
                .statusCode(200)
                .message("Đã đánh dấu thông báo là đã đọc")
                .data(data)
                .build());
    }

    // API-35: Tạo thông báo mới cho người dùng
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<NotificationResponse>> createNotification(
            @Valid @RequestBody NotificationCreateRequest request) {

        NotificationResponse data = notificationService.createNotification(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<NotificationResponse>builder()
                .success(true)
                .statusCode(201)
                .message("Tạo thông báo thành công")
                .data(data)
                .build());
    }

    // API-36: Xóa thông báo
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{notification_id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable("notification_id") Integer notificationId) {

        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .statusCode(200)
                .message("Xóa thông báo thành công")
                .build());
    }
}