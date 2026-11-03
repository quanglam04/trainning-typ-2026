package com.example.coursemanagement_finalprojectmodule03.model.dto.res;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CourseResponse {
    private Integer courseId;
    private String title;
    private String description;
    private Integer teacherId;
    private String teacherName;
    private BigDecimal price;
    private Integer durationHours;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Double averageRating;
    private Long totalReviews;
}
