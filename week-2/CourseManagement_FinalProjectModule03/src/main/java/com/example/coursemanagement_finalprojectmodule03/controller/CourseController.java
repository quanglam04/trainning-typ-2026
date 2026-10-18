package com.example.coursemanagement_finalprojectmodule03.controller;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.CourseCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.CourseUpdateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.CourseUpdateStatusRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.*;
import com.example.coursemanagement_finalprojectmodule03.model.dto.wrapper.ApiResponse;
import com.example.coursemanagement_finalprojectmodule03.model.service.ICourseService;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final ICourseService courseService;

    // API-10_28_29_32: Lấy danh sách tất cả khóa học và hỗ trợ tìm kiếm, lọc
    @PreAuthorize("isAuthenticated()")
    @GetMapping
    public ResponseEntity<ApiResponse<Page<CourseListResponse>>> getAllCourses(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer teacher_id,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal CustomUserDetails currentUser,
            Pageable pageable) {

        Page<CourseListResponse> data = courseService.getAllCourses(search, teacher_id, status, currentUser, pageable);
        return ResponseEntity.ok(ApiResponse.<Page<CourseListResponse>>builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách khóa học thành công")
                .data(data)
                .build());
    }

    // API-11: Lấy thông tin chi tiết một khóa học
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/{course_id}")
    public ResponseEntity<ApiResponse<CourseDetailResponse>> getCourseById(
            @PathVariable("course_id") Integer courseId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        CourseDetailResponse data = courseService.getCourseById(courseId, currentUser);
        return ResponseEntity.ok(ApiResponse.<CourseDetailResponse>builder()
                .success(true)
                .statusCode(200)
                .message("Lấy chi tiết khóa học thành công")
                .data(data)
                .build());
    }

    // API-12: Tạo khóa học mới
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse<CourseResponse>> createCourse(@Valid @RequestBody CourseCreateRequest request) {
        CourseResponse data = courseService.createCourse(request);
        return ResponseEntity.status(201).body(ApiResponse.<CourseResponse>builder()
                .success(true)
                .statusCode(201)
                .message("Tạo khóa học mới thành công")
                .data(data)
                .build());
    }

    // API-13: Cập nhật thông tin chi tiết khóa học
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{course_id}")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourse(
            @PathVariable("course_id") Integer courseId,
            @Valid @RequestBody CourseUpdateRequest request) {

        CourseResponse data = courseService.updateCourse(courseId, request);
        return ResponseEntity.ok(ApiResponse.<CourseResponse>builder()
                .success(true)
                .statusCode(200)
                .message("Cập nhật thông tin khóa học thành công")
                .data(data)
                .build());
    }

    // Week 2 bổ sung: PATCH chỉ cập nhật những trường được gửi trong JSON body.
    // Dùng CourseUpdateRequest + MapStruct NullValuePropertyMappingStrategy.IGNORE.
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{course_id}")
    public ResponseEntity<ApiResponse<CourseResponse>> patchCourse(
            @PathVariable("course_id") Integer courseId,
            @Valid @RequestBody CourseUpdateRequest request) {

        CourseResponse data = courseService.updateCourse(courseId, request);
        return ResponseEntity.ok(ApiResponse.<CourseResponse>builder()
                .success(true)
                .statusCode(200)
                .message("Cập nhật một phần khóa học thành công")
                .data(data)
                .build());
    }

    // API-14: Cập nhật trạng thái khóa học
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{course_id}/status")
    public ResponseEntity<ApiResponse<CourseResponse>> updateCourseStatus(
            @PathVariable("course_id") Integer courseId,
            @Valid @RequestBody CourseUpdateStatusRequest request) {

        CourseResponse data = courseService.updateCourseStatus(courseId, request);
        return ResponseEntity.ok(ApiResponse.<CourseResponse>builder()
                .success(true)
                .statusCode(200)
                .message("Cập nhật trạng thái khóa học thành công")
                .data(data)
                .build());
    }

    // API-15: Xóa khóa học
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{course_id}")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable("course_id") Integer courseId) {
        courseService.deleteCourse(courseId);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .statusCode(200)
                .message("Xóa khóa học thành công")
                .build());
    }
}
