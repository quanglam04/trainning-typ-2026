package com.example.coursemanagement_finalprojectmodule03.model.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
@Data
@Builder
@NoArgsConstructor
public class StudentEnrolledCourseResponse {
    private Integer enrollmentId;
    private Integer courseId;
    private String courseTitle;
    private String status;
    private BigDecimal progressPercentage;
    private Long completedLessons;
    private Long totalLessons;

    public StudentEnrolledCourseResponse(Integer enrollmentId, Integer courseId, String courseTitle,
                                         String status, BigDecimal progressPercentage,
                                         long completedLessons, long totalLessons) {
        this.enrollmentId = enrollmentId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
        this.status = status;
        this.progressPercentage = progressPercentage;
        this.completedLessons = completedLessons;
        this.totalLessons = totalLessons;
    }
}
