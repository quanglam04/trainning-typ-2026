package com.example.coursemanagement_finalprojectmodule03.model.dto.req;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CourseUpdateRequest
{
    private String title;

    private String description;

    private Integer teacherId;

    @Min(value = 0, message = "Giá khóa học không được âm")
    private BigDecimal price;

    @Min(value = 0, message = "Thời lượng không hợp lệ")
    private Integer durationHours;
}
