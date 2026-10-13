package com.example.coursemanagement_finalprojectmodule03.model.service;

import com.example.coursemanagement_finalprojectmodule03.model.dto.res.ReportStudentProgressResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.ReportTeacherOverviewResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.ReportTopCourseResponse;

import java.util.List;

public interface IReportService {
    // API 37: Lấy Top khóa học phổ biến nhất
    List<ReportTopCourseResponse> getTopCourses(int limit);

    // API 38: Thống kê tiến độ học của sinh viên
    ReportStudentProgressResponse getStudentProgressReport(Integer studentId);

    // API 39: Thống kê tổng quan giảng viên
    ReportTeacherOverviewResponse getTeacherOverviewReport(Integer teacherId);
}
