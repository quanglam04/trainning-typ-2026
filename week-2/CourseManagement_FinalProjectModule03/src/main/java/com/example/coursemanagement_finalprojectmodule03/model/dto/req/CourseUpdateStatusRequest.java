package com.example.coursemanagement_finalprojectmodule03.model.dto.req;

import com.example.coursemanagement_finalprojectmodule03.constant.CourseStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CourseUpdateStatusRequest
{
    @NotNull(message = "Trạng thái không được để trống")
    private CourseStatus status;
}
