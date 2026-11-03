package com.example.coursemanagement_finalprojectmodule03.model.service;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.ReviewCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.ReviewUpdateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.ReviewResponse;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import org.springframework.data.domain.Page;

public interface IReviewService {
    // 1. Tạo mới đánh giá (POST)
    ReviewResponse createReview(Integer courseId, ReviewCreateRequest request, CustomUserDetails currentUser);

    // 2. Cập nhật linh hoạt từng trường (PUT/PATCH)
    ReviewResponse updateReview(Integer reviewId, ReviewUpdateRequest request, CustomUserDetails currentUser);

    // 3. Lấy danh sách đánh giá của khóa học (GET)
    Page<ReviewResponse> getReviewsByCourse(Integer courseId, int page, int size);

    // 4. Xóa đánh giá (DELETE)
    void deleteReview(Integer reviewId, CustomUserDetails currentUser);
}