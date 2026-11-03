package com.example.coursemanagement_finalprojectmodule03.controller;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.LessonCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.LessonPublishRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.LessonUpdateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.LessonListResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.LessonPreviewResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.LessonResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.wrapper.ApiResponse;
import com.example.coursemanagement_finalprojectmodule03.model.service.ILessonService;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LessonController {

    private final ILessonService lessonService;

    // API-16: Lấy danh sách tất cả bài học trong một khóa học
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/courses/{course_id}/lessons")
    public ResponseEntity<ApiResponse<List<LessonListResponse>>> getLessonsByCourse(
            @PathVariable("course_id") Integer courseId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        List<LessonListResponse> data = lessonService.getLessonsByCourse(courseId, currentUser);
        return ResponseEntity.ok(ApiResponse.<List<LessonListResponse>>builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách bài học thành công")
                .data(data)
                .build());
    }
    // API-17: Lấy thông tin chi tiết một bài học
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/lessons/{lesson_id}")
    public ResponseEntity<ApiResponse<LessonResponse>> getLessonById(
            @PathVariable("lesson_id") Integer lessonId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        LessonResponse data = lessonService.getLessonById(lessonId, currentUser);
        return ResponseEntity.ok(ApiResponse.<LessonResponse>builder()
                .success(true)
                .statusCode(200)
                .message("Lấy thông tin chi tiết bài học thành công")
                .data(data)
                .build());
    }

    // API-18: Thêm bài học mới vào khóa học
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PostMapping(value = "/courses/{course_id}/lessons", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<LessonResponse>> createLesson(
            @PathVariable("course_id") Integer courseId,
            @Valid @ModelAttribute LessonCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        LessonResponse data = lessonService.createLesson(courseId, request, currentUser);
        return ResponseEntity.status(201).body(ApiResponse.<LessonResponse>builder()
                .success(true)
                .statusCode(201)
                .message("Tạo bài học mới thành công")
                .data(data)
                .build());
    }

    // API-19: Cập nhật thông tin bài học
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PutMapping(value = "/lessons/{lesson_id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<LessonResponse>> updateLesson(
            @PathVariable("lesson_id") Integer lessonId,
            @Valid @ModelAttribute LessonUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        LessonResponse data = lessonService.updateLesson(lessonId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.<LessonResponse>builder()
                .success(true)
                .statusCode(200)
                .message("Cập nhật bài học thành công")
                .data(data)
                .build());
    }

    // API-20: Cập nhật trạng thái hiển thị bài học
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @PutMapping("/lessons/{lesson_id}/publish")
    public ResponseEntity<ApiResponse<LessonResponse>> updateLessonPublishStatus(
            @PathVariable("lesson_id") Integer lessonId,
            @Valid @RequestBody LessonPublishRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        LessonResponse data = lessonService.updateLessonPublishStatus(lessonId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.<LessonResponse>builder()
                .success(true)
                .statusCode(200)
                .message("Cập nhật trạng thái hiển thị thành công")
                .data(data)
                .build());
    }

    // API-21: Xóa bài học
    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER')")
    @DeleteMapping("/lessons/{lesson_id}")
    public ResponseEntity<ApiResponse<Void>> deleteLesson(
            @PathVariable("lesson_id") Integer lessonId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        lessonService.deleteLesson(lessonId, currentUser);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .statusCode(200)
                .message("Xóa bài học thành công")
                .build());
    }

    // API-44: Lấy nội dung xem trước của bài học
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/lessons/{lesson_id}/content_preview")
    public ResponseEntity<ApiResponse<LessonPreviewResponse>> getContentPreview(
            @PathVariable("lesson_id") Integer lessonId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        LessonPreviewResponse data = lessonService.getContentPreview(lessonId, currentUser);
        return ResponseEntity.ok(ApiResponse.<LessonPreviewResponse>builder()
                .success(true)
                .statusCode(200)
                .message("Lấy nội dung xem trước thành công")
                .data(data)
                .build());
    }
}