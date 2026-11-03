package com.example.coursemanagement_finalprojectmodule03.controller;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.ReportStudentProgressResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.ReportTeacherOverviewResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.ReportTopCourseResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.wrapper.ApiResponse;
import com.example.coursemanagement_finalprojectmodule03.model.service.IReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final IReportService reportService;

    // API 37: Lấy danh sách Top khóa học phổ biến nhất
    @GetMapping("/top_courses")
    public ResponseEntity<ApiResponse<List<ReportTopCourseResponse>>> getTopCourses(
            @RequestParam(value = "limit", defaultValue = "10") int limit) {

        List<ReportTopCourseResponse> data = reportService.getTopCourses(limit);
        return ResponseEntity.ok(ApiResponse.<List<ReportTopCourseResponse>>builder()
                .success(true)
                .statusCode(200)
                .message("Lấy báo cáo top khóa học thành công")
                .data(data)
                .build());
    }

    // API 38: Thống kê tiến độ học của một sinh viên cụ thể
    @GetMapping("/student_progress/{student_id}")
    public ResponseEntity<ApiResponse<ReportStudentProgressResponse>> getStudentProgress(
            @PathVariable("student_id") Integer studentId) {

        ReportStudentProgressResponse data = reportService.getStudentProgressReport(studentId);
        return ResponseEntity.ok(ApiResponse.<ReportStudentProgressResponse>builder()
                .success(true)
                .statusCode(200)
                .message("Lấy báo cáo tiến độ sinh viên thành công")
                .data(data)
                .build());
    }

    // API 39: Thống kê tổng quan các khóa học của giảng viên
    @GetMapping("/teacher_courses_overview/{teacher_id}")
    public ResponseEntity<ApiResponse<ReportTeacherOverviewResponse>> getTeacherOverview(
            @PathVariable("teacher_id") Integer teacherId) {

        ReportTeacherOverviewResponse data = reportService.getTeacherOverviewReport(teacherId);
        return ResponseEntity.ok(ApiResponse.<ReportTeacherOverviewResponse>builder()
                .success(true)
                .statusCode(200)
                .message("Lấy báo cáo tổng quan giảng viên thành công")
                .data(data)
                .build());
    }
}