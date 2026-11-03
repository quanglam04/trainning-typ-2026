package com.example.coursemanagement_finalprojectmodule03.model.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportTeacherOverviewResponse {
    private Integer teacherId;
    private String teacherName;
    private Long totalCourses;
    private Long totalPublishedCourses;
    private Long totalStudentsEnrolled;
    private List<CourseStatisticResponse> courseDetails;
}