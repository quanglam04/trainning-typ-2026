package com.example.coursemanagement_finalprojectmodule03.model.dto.res;
import com.example.coursemanagement_finalprojectmodule03.constant.EnrollmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentResponse {
    private Integer enrollmentId;
    private LocalDateTime enrollmentDate;
    private EnrollmentStatus status;
    private LocalDateTime completionDate;
    private BigDecimal progressPercentage;

    private Integer courseId;
    private String courseTitle;
}