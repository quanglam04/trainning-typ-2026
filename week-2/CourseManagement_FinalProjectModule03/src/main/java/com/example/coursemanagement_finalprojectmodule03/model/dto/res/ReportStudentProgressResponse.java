package com.example.coursemanagement_finalprojectmodule03.model.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportStudentProgressResponse {
    private Integer studentId;
    private String fullName;
    private Long totalEnrolledCourses;
    private Long completedCourses;
    private BigDecimal averageProgress;
    private List<StudentEnrolledCourseResponse> enrolledCoursesDetails;
}