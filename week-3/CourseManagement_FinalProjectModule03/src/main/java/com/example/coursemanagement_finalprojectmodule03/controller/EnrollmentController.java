package com.example.coursemanagement_finalprojectmodule03.controller;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.EnrollmentCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.EnrollmentDetailResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.EnrollmentResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.wrapper.ApiResponse;
import com.example.coursemanagement_finalprojectmodule03.model.service.IEnrollmentService;
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
@RequestMapping("/api/enrollments")
@RequiredArgsConstructor
@PreAuthorize("hasRole('STUDENT')")
public class EnrollmentController {

    private final IEnrollmentService enrollmentService;

    // API 22: Danh sách các khóa học sinh viên đã đăng ký
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EnrollmentResponse>>> getMyEnrollments(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        Page<EnrollmentResponse> data = enrollmentService.getMyEnrollments(currentUser, page, size);
        return ResponseEntity.ok(ApiResponse.<Page<EnrollmentResponse>>builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách khóa học đã đăng ký thành công")
                .data(data)
                .build());
    }

    // API 23: Đăng ký một khóa học mới
    @PostMapping
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enrollCourse(
            @Valid @RequestBody EnrollmentCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        EnrollmentResponse data = enrollmentService.enrollCourse(request, currentUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<EnrollmentResponse>builder()
                .success(true)
                .statusCode(201)
                .message("Đăng ký khóa học thành công")
                .data(data)
                .build());
    }

    // API 24: Lấy chi tiết thông tin đăng ký (tiến độ học)
    @GetMapping("/{enrollment_id}")
    public ResponseEntity<ApiResponse<EnrollmentDetailResponse>> getEnrollmentDetail(
            @PathVariable("enrollment_id") Integer enrollmentId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        EnrollmentDetailResponse data = enrollmentService.getEnrollmentDetail(enrollmentId, currentUser);
        return ResponseEntity.ok(ApiResponse.<EnrollmentDetailResponse>builder()
                .success(true)
                .statusCode(200)
                .message("Lấy chi tiết tiến độ khóa học thành công")
                .data(data)
                .build());
    }

    // API 25: Đánh dấu bài học hoàn thành
    @PutMapping("/{enrollment_id}/complete_lesson/{lesson_id}")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> completeLesson(
            @PathVariable("enrollment_id") Integer enrollmentId,
            @PathVariable("lesson_id") Integer lessonId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        EnrollmentResponse data = enrollmentService.completeLesson(enrollmentId, lessonId, currentUser);
        return ResponseEntity.ok(ApiResponse.<EnrollmentResponse>builder()
                .success(true)
                .statusCode(200)
                .message("Cập nhật tiến độ bài học thành công")
                .data(data)
                .build());
    }
}