package com.example.coursemanagement_finalprojectmodule03.mq;

import com.example.coursemanagement_finalprojectmodule03.model.entity.Notification;
import com.example.coursemanagement_finalprojectmodule03.model.entity.User;
import com.example.coursemanagement_finalprojectmodule03.model.repo.INotificationRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IUserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EnrollmentNotificationHandler {
    private final ProcessedEventRepository processedRepo;
    private final IUserRepo userRepo;
    private final INotificationRepo notificationRepo;

    @Transactional
    public void handle(EnrollmentCreatedEvent event) {
        if (processedRepo.existsById(event.eventId())) return;

        User student = userRepo.findById(event.studentId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy học viên: " + event.studentId()));

        notificationRepo.save(Notification.builder()
                .user(student)
                .type("ENROLLMENT")
                .message("Bạn đã đăng ký khóa học: " + event.courseTitle())
                .targetUrl("/courses/" + event.courseId())
                .build());

        // Thông báo và event đã xử lý được lưu cùng transaction.
        // eventId là khóa duy nhất, tránh ghi hai thông báo khi message tới trùng.
        processedRepo.saveAndFlush(new ProcessedEvent(event.eventId()));
    }
}
