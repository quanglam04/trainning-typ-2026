package com.example.coursemanagement_finalprojectmodule03.model.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EnrollmentCreateRequest {
    @NotNull(message = "ID khóa học (courseId) không được để trống")
    private Integer courseId;
}
