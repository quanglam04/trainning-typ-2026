package com.example.coursemanagement_finalprojectmodule03.model.service;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.NotificationCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.NotificationResponse;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface INotificationService {

    // API 33: Lấy danh sách thông báo của tôi (mới nhất xếp trên)
    Page<NotificationResponse> getMyNotifications(CustomUserDetails currentUser, int page, int size);

    // API 34: Đánh dấu thông báo đã đọc
    @Transactional
    NotificationResponse markAsRead(Integer notificationId, CustomUserDetails currentUser);

    // API 35: Tạo thông báo mới (Chỉ ADMIN)[cite: 1, 2]
    @Transactional
    NotificationResponse createNotification(NotificationCreateRequest request);

    // API 36: Xóa thông báo (Chỉ ADMIN)
    @Transactional
    void deleteNotification(Integer notificationId);
}
