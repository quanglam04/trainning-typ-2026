package com.example.coursemanagement_finalprojectmodule03.model.dto.res;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseListResponse {
    //bỏ descrip cho gọn
    private Integer courseId;
    private String title;
    private Integer teacherId;
    private String teacherName;
    private BigDecimal price;
    private Integer durationHours;
    private String status;
    private LocalDateTime createdAt;
}