package com.example.coursemanagement_finalprojectmodule03.controller;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.ReviewCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.ReviewUpdateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.ReviewResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.wrapper.ApiResponse;
import com.example.coursemanagement_finalprojectmodule03.model.service.IReviewService;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final IReviewService reviewService;

    // API-40: Lấy danh sách đánh giá hoặc bình luận về khóa học
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/courses/{course_id}/reviews")
    public ResponseEntity<ApiResponse<Page<ReviewResponse>>> getReviewsByCourse(
            @PathVariable("course_id") Integer courseId,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {

        Page<ReviewResponse> data = reviewService.getReviewsByCourse(courseId, page, size);
        return ResponseEntity.ok(ApiResponse.<Page<ReviewResponse>>builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách đánh giá thành công")
                .data(data)
                .build());
    }

    // API-41: Sinh viên gửi đánh giá hoặc bình luận về khóa học
    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/courses/{course_id}/reviews")
    public ResponseEntity<ApiResponse<ReviewResponse>> createReview(
            @PathVariable("course_id") Integer courseId,
            @Valid @RequestBody ReviewCreateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        ReviewResponse data = reviewService.createReview(courseId, request, currentUser);
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(ApiResponse.<ReviewResponse>builder()
                .success(true)
                .statusCode(201)
                .message("Đánh giá khóa học thành công")
                .data(data)
                .build());
    }

    // API-42: Cập nhật đánh giá hoặc bình luận (Quyền Owner Service tự check)
    @PreAuthorize("isAuthenticated()")
    @PutMapping("/reviews/{review_id}")
    public ResponseEntity<ApiResponse<ReviewResponse>> updateReview(
            @PathVariable("review_id") Integer reviewId,
            @Valid @RequestBody ReviewUpdateRequest request,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        ReviewResponse data = reviewService.updateReview(reviewId, request, currentUser);
        return ResponseEntity.ok(ApiResponse.<ReviewResponse>builder()
                .success(true)
                .statusCode(200)
                .message("Cập nhật đánh giá thành công")
                .data(data)
                .build());
    }

    // API-43: Xóa đánh giá hoặc bình luận (Quyền Owner/Admin Service tự check)
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/reviews/{review_id}")
    public ResponseEntity<ApiResponse<Void>> deleteReview(
            @PathVariable("review_id") Integer reviewId,
            @AuthenticationPrincipal CustomUserDetails currentUser) {

        reviewService.deleteReview(reviewId, currentUser);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .statusCode(200)
                .message("Xóa đánh giá thành công")
                .data(null)
                .build());
    }
}
