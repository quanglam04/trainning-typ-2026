package com.example.coursemanagement_finalprojectmodule03.model.service.impl;

import com.example.coursemanagement_finalprojectmodule03.constant.CourseStatus;
import com.example.coursemanagement_finalprojectmodule03.constant.Role;
import com.example.coursemanagement_finalprojectmodule03.exception.BadRequestException;
import com.example.coursemanagement_finalprojectmodule03.exception.ResourceNotFoundException;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.*;
import com.example.coursemanagement_finalprojectmodule03.model.entity.User;
import com.example.coursemanagement_finalprojectmodule03.model.repo.ICourseRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IEnrollmentRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IUserRepo;
import com.example.coursemanagement_finalprojectmodule03.model.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements IReportService {

    private final IEnrollmentRepo enrollmentRepo;
    private final ICourseRepo courseRepo;
    private final IUserRepo userRepo;

    // API 37: Lấy Top khóa học phổ biến nhất
    @Override
    public List<ReportTopCourseResponse> getTopCourses(int limit) {
        // Trả thẳng dữ liệu từ Repo nhờ sức mạnh của JPQL Constructor
        return enrollmentRepo.getTopCoursesReport(PageRequest.of(0, limit));
    }

    // API 38: Thống kê tiến độ học của sinh viên
    @Override
    public ReportStudentProgressResponse getStudentProgressReport(Integer studentId) {
        User student = userRepo.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng!"));

        if (student.getRole() != Role.STUDENT) {
            throw new BadRequestException("Người dùng này không phải là Sinh viên!");
        }

        // 1. Gọi 1 query duy nhất lấy trọn gói dữ liệu
        List<StudentEnrolledCourseResponse> courseDetails = enrollmentRepo.getStudentEnrolledCoursesDetails(studentId);

        // 2. Tính toán tổng hợp trên RAM
        long totalEnrolled = courseDetails.size();
        long completedCourses = courseDetails.stream()
                .filter(c -> "COMPLETED".equals(c.getStatus())) // So sánh chuỗi vì DTO đang dùng String
                .count();

        // 3. Tính điểm trung bình (Xử lý an toàn tránh chia cho 0)
        BigDecimal avgProgress = BigDecimal.ZERO;
        if (totalEnrolled > 0) {
            BigDecimal totalProgressSum = courseDetails.stream()
                    .map(StudentEnrolledCourseResponse::getProgressPercentage)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            avgProgress = totalProgressSum.divide(BigDecimal.valueOf(totalEnrolled), 2, RoundingMode.HALF_UP);
        }

        return ReportStudentProgressResponse.builder()
                .studentId(student.getId())
                .fullName(student.getFullName())
                .totalEnrolledCourses(totalEnrolled)
                .completedCourses(completedCourses)
                .averageProgress(avgProgress)
                .enrolledCoursesDetails(courseDetails)
                .build();
    }

    // API 39: Thống kê tổng quan giảng viên
    @Override
    public ReportTeacherOverviewResponse getTeacherOverviewReport(Integer teacherId) {
        User teacher = userRepo.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng!"));

        if (teacher.getRole() != Role.TEACHER) {
            throw new BadRequestException("Người dùng này không phải là Giảng viên!");
        }

        // Lấy danh sách từ courseRepo (đã bao gồm DRAFT và khóa 0 sinh viên)
        List<CourseStatisticResponse> courseDetails = courseRepo.getCourseStatisticsByTeacherId(teacherId);

        return ReportTeacherOverviewResponse.builder()
                .teacherId(teacher.getId())
                .teacherName(teacher.getFullName())
                .totalCourses(courseRepo.countByTeacherId(teacherId))
                .totalPublishedCourses(courseRepo.countByTeacherIdAndStatus(teacherId, CourseStatus.PUBLISHED))
                .totalStudentsEnrolled(enrollmentRepo.countTotalStudentsByTeacherId(teacherId))
                .courseDetails(courseDetails)
                .build();
    }
}
