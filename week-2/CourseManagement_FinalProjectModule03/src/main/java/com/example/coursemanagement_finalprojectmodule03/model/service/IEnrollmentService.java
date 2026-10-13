package com.example.coursemanagement_finalprojectmodule03.model.service;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.EnrollmentCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.EnrollmentDetailResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.EnrollmentResponse;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface IEnrollmentService {

    // API 22: Danh sách các khóa học sinh viên đã đăng ký
    Page<EnrollmentResponse> getMyEnrollments(CustomUserDetails currentUser, int page, int size);

    // API 23: Đăng ký một khóa học mới
    @Transactional
    EnrollmentResponse enrollCourse(EnrollmentCreateRequest request, CustomUserDetails currentUser);

    // API 24: Lấy chi tiết thông tin đăng ký và tiến độ học tập[cite: 1, 2]
    EnrollmentDetailResponse getEnrollmentDetail(Integer enrollmentId, CustomUserDetails currentUser);

    // API 25: Đánh dấu bài học đã hoàn thành & tính lại tiến độ[cite: 1, 2]
    @Transactional
    EnrollmentResponse completeLesson(Integer enrollmentId, Integer lessonId, CustomUserDetails currentUser);
}
