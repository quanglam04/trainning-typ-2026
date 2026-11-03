package com.example.coursemanagement_finalprojectmodule03.model.repo;

import com.example.coursemanagement_finalprojectmodule03.constant.CourseStatus;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.CourseStatisticResponse;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

import java.util.Optional;

public interface ICourseRepo extends JpaRepository<Course, Integer>, JpaSpecificationExecutor<Course> {
    Optional<Course> findByTitle(String title);
    long countByTeacherId(Integer teacherId);

    // Đếm số khóa học CÓ TRẠNG THÁI CỤ THỂ của 1 giảng viên (VD: chỉ đếm khóa PUBLISHED)
    long countByTeacherIdAndStatus(Integer teacherId, CourseStatus status);

    // API 39: Lấy toàn bộ khóa học của GV (kể cả DRAFT và 0 học viên) bằng LEFT JOIN
    @Query("SELECT new com.example.coursemanagement_finalprojectmodule03.model.dto.res.CourseStatisticResponse(" +
            "c.id, c.title, CAST(c.status AS string), COUNT(e.id)) " +
            "FROM Course c " +
            "LEFT JOIN Enrollment e ON e.course.id = c.id " +
            "WHERE c.teacher.id = :teacherId " +
            "GROUP BY c.id, c.title, c.status")
    List<CourseStatisticResponse> getCourseStatisticsByTeacherId(@Param("teacherId") Integer teacherId);
}
