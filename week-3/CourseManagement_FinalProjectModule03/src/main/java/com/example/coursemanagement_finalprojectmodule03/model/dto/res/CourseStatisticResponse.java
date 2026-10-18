package com.example.coursemanagement_finalprojectmodule03.model.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseStatisticResponse {
    private Integer courseId;
    private String title;
    private String status;
    private Long enrolledStudentsCount;
}
