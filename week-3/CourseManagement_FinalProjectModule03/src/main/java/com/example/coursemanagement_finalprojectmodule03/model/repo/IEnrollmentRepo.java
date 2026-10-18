package com.example.coursemanagement_finalprojectmodule03.model.repo;

import com.example.coursemanagement_finalprojectmodule03.constant.EnrollmentStatus;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.ReportTopCourseResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.StudentEnrolledCourseResponse;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Enrollment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface IEnrollmentRepo extends JpaRepository<Enrollment, Integer> {

    Page<Enrollment> findByStudentId(Integer studentId, Pageable pageable);

    Optional<Enrollment> findByIdAndStudentId(Integer id, Integer studentId);

    boolean existsByStudentIdAndCourseId(Integer studentId, Integer courseId);

    // API 37: (Đã sửa lại đúng tên ReportTopCourseResponse)
    @Query("SELECT new com.example.coursemanagement_finalprojectmodule03.model.dto.res.ReportTopCourseResponse(" +
            "e.course.id, e.course.title, e.course.teacher.fullName, COUNT(e.id)) " +
            "FROM Enrollment e " +
            "GROUP BY e.course.id, e.course.title, e.course.teacher.fullName " +
            "ORDER BY COUNT(e.id) DESC")
    List<ReportTopCourseResponse> getTopCoursesReport(Pageable pageable);

    // API 38: Lấy danh sách khóa học và đếm bài học
    @Query("SELECT new com.example.coursemanagement_finalprojectmodule03.model.dto.res.StudentEnrolledCourseResponse(" +
            "e.id, " +
            "e.course.id, " +
            "e.course.title, " +
            "CAST(e.status AS string), " +
            "e.progressPercentage, " +
            "(SELECT COUNT(lp.id) FROM LessonProgress lp WHERE lp.enrollment.id = e.id AND lp.isCompleted = true), " +
            "(SELECT COUNT(l.id) FROM Lesson l WHERE l.course.id = e.course.id AND l.isPublished = true)) " +
            "FROM Enrollment e " +
            "WHERE e.student.id = :studentId")
    List<StudentEnrolledCourseResponse> getStudentEnrolledCoursesDetails(@Param("studentId") Integer studentId);

    // Đếm số khóa học mà 1 sinh viên đã hoàn thành
    long countByStudentIdAndStatus(Integer studentId, EnrollmentStatus status);

    // Đếm tổng số sinh viên đăng ký các khóa học của 1 giảng viên cụ thể
    @Query("SELECT COUNT(e.id) FROM Enrollment e WHERE e.course.teacher.id = :teacherId")
    long countTotalStudentsByTeacherId(@Param("teacherId") Integer teacherId);

    Optional<Enrollment> findByStudentIdAndCourseId(Integer studentId, Integer courseId);

    boolean existsByCourseId(Integer courseId);
}