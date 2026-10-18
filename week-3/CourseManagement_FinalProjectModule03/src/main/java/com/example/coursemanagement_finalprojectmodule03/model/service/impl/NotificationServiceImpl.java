package com.example.coursemanagement_finalprojectmodule03.model.service.impl;

import com.example.coursemanagement_finalprojectmodule03.exception.ResourceNotFoundException;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.NotificationCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.NotificationResponse;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Notification;
import com.example.coursemanagement_finalprojectmodule03.model.entity.User;
import com.example.coursemanagement_finalprojectmodule03.model.mapper.NotificationMapper;
import com.example.coursemanagement_finalprojectmodule03.model.repo.INotificationRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IUserRepo;
import com.example.coursemanagement_finalprojectmodule03.model.service.INotificationService;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements INotificationService {

    private final INotificationRepo notificationRepo;
    private final IUserRepo userRepo;
    private final NotificationMapper notificationMapper;

    // API 33: Lấy danh sách thông báo của tôi (mới nhất xếp trên)
    @Override
    public Page<NotificationResponse> getMyNotifications(CustomUserDetails currentUser, int page, int size) {
        Integer userId = currentUser.getUser().getId();
        Pageable pageable = PageRequest.of(page, size);

        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId, pageable)
                .map(notificationMapper::toResponse);
    }

    // API 34: Đánh dấu thông báo đã đọc
    @Transactional
    @Override
    public NotificationResponse markAsRead(Integer notificationId, CustomUserDetails currentUser) {
        Integer currentUserId = currentUser.getUser().getId();

        // Bước 1: Kiểm tra thông báo có tồn tại trong hệ thống hay không -> Ném 404
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo với ID: " + notificationId));

        // Bước 2: Kiểm tra quyền sở hữu (Chỉ người nhận mới được đọc) -> Ném 403
        if (!notification.getUser().getId().equals(currentUserId)) {
            throw new AccessDeniedException("Bạn không có quyền thao tác trên thông báo của người khác!");
        }

        // Bước 3: Cập nhật trạng thái
        notification.setRead(true);
        return notificationMapper.toResponse(notificationRepo.saveAndFlush(notification));
    }

    // API 35: Tạo thông báo mới (Chỉ ADMIN)
    @Transactional
    @Override
    public NotificationResponse createNotification(NotificationCreateRequest request) {
        User targetUser = userRepo.findById(request.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng nhận thông báo!"));

        Notification notification = Notification.builder()
                .user(targetUser)
                .message(request.getMessage().trim())
                .type(request.getType())
                .targetUrl(request.getTargetUrl())
                .build();

        return notificationMapper.toResponse(notificationRepo.save(notification));
    }

    // API 36: Xóa thông báo (Chỉ ADMIN)
    @Transactional
    @Override
    public void deleteNotification(Integer notificationId) {
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thông báo!"));
        notificationRepo.delete(notification);
    }
}