package com.example.coursemanagement_finalprojectmodule03.model.service.impl;

import com.example.coursemanagement_finalprojectmodule03.constant.Role;
import com.example.coursemanagement_finalprojectmodule03.exception.BadRequestException;
import com.example.coursemanagement_finalprojectmodule03.exception.ConflictException;
import com.example.coursemanagement_finalprojectmodule03.exception.ResourceNotFoundException;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.ReviewCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.ReviewUpdateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.ReviewResponse;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Course;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Enrollment;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Review;
import com.example.coursemanagement_finalprojectmodule03.model.mapper.ReviewMapper;
import com.example.coursemanagement_finalprojectmodule03.model.repo.ICourseRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IEnrollmentRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IReviewRepo;
import com.example.coursemanagement_finalprojectmodule03.model.service.IReviewService;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements IReviewService {

    private final IReviewRepo reviewRepo;
    private final ICourseRepo courseRepo;
    private final IEnrollmentRepo enrollmentRepo;
    private final ReviewMapper reviewMapper;

    @Transactional
    @Override
    public ReviewResponse createReview(Integer courseId, ReviewCreateRequest request, CustomUserDetails currentUser) {
        if (currentUser.getUser().getRole() != Role.STUDENT) {
            throw new AccessDeniedException("Chỉ sinh viên mới được quyền đánh giá khóa học!");
        }

        Integer studentId = currentUser.getUser().getId();

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học!"));

        // 1. Lấy bản ghi Enrollment ra thay vì chỉ check tồn tại
        Enrollment enrollment = enrollmentRepo.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new BadRequestException("Bạn chưa đăng ký khóa học này nên không thể đánh giá!"));

        // 2. CHECK TIẾN ĐỘ THEO ĐÚNG SRS (Phải học ít nhất > 0% mới được rate)
        if (enrollment.getProgressPercentage().compareTo(BigDecimal.ZERO) == 0) {
            throw new BadRequestException("Bạn phải hoàn thành ít nhất 1 bài học trước khi đưa ra đánh giá!");
        }

        // Chặn trùng lặp: Nếu đã review rồi thì bắt buộc phải dùng API Update
        if (reviewRepo.findByCourseIdAndStudentId(courseId, studentId).isPresent()) {
            throw new ConflictException("Bạn đã đánh giá khóa học này rồi! Hãy sử dụng chức năng chỉnh sửa đánh giá.");
        }

        Review newReview = Review.builder()
                .course(course)
                .student(currentUser.getUser())
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return reviewMapper.toResponse(reviewRepo.save(newReview));
    }

    @Transactional
    @Override
    public ReviewResponse updateReview(Integer reviewId, ReviewUpdateRequest request, CustomUserDetails currentUser) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá!"));

        // Chỉ chính chủ bài review mới có quyền chỉnh sửa
        if (!review.getStudent().getId().equals(currentUser.getUser().getId())) {
            throw new AccessDeniedException("Bạn không có quyền chỉnh sửa đánh giá của người khác!");
        }

        // Cập nhật linh hoạt: Trường nào có truyền lên thì mới sửa
        if (request.getRating() != null) {
            review.setRating(request.getRating());
        }

        if (request.getComment() != null && !request.getComment().trim().isEmpty()) {
            review.setComment(request.getComment().trim());
        }

        return reviewMapper.toResponse(reviewRepo.save(review));
    }

    @Override
    public Page<ReviewResponse> getReviewsByCourse(Integer courseId, int page, int size) {
        if (!courseRepo.existsById(courseId)) {
            throw new ResourceNotFoundException("Không tìm thấy khóa học!");
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return reviewRepo.findByCourseId(courseId, pageable).map(reviewMapper::toResponse);
    }

    @Transactional
    @Override
    public void deleteReview(Integer reviewId, CustomUserDetails currentUser) {
        Review review = reviewRepo.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đánh giá!"));

        boolean isAdmin = currentUser.getUser().getRole() == Role.ADMIN;
        boolean isOwner = review.getStudent().getId().equals(currentUser.getUser().getId());

        if (!isAdmin && !isOwner) {
            throw new AccessDeniedException("Bạn không có quyền xóa đánh giá này!");
        }

        review.setDeleted(true);
        reviewRepo.save(review);
    }
}