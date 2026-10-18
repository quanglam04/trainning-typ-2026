package com.example.coursemanagement_finalprojectmodule03.model.repo;

import com.example.coursemanagement_finalprojectmodule03.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IReviewRepo extends JpaRepository<Review, Integer> {

    // 1. Lấy danh sách review của 1 khóa học
    Page<Review> findByCourseId(Integer courseId, Pageable pageable);

    // 2. Tìm review của 1 sinh viên cụ thể trong 1 khóa học (Dùng để check xem đã rate chưa)
    Optional<Review> findByCourseIdAndStudentId(Integer courseId, Integer studentId);

    // 3. Tính điểm đánh giá trung bình của 1 khóa học (Trả về double)
    @Query("SELECT COALESCE(AVG(r.rating), 0.0) FROM Review r WHERE r.course.id = :courseId")
    Double calculateAverageRatingByCourseId(@Param("courseId") Integer courseId);

    // 4. Đếm tổng số lượt đánh giá của 1 khóa học
    long countByCourseId(Integer courseId);
}