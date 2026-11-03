package com.example.coursemanagement_finalprojectmodule03.model.dto.req;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CourseCreateRequest {
    @NotBlank(message = "Tiêu đề khóa học không được để trống")
    private String title;

    private String description;

    @NotNull(message = "ID Giảng viên phụ trách không được để trống")
    private Integer teacherId;

    @Min(value = 0, message = "Giá khóa học không được âm")
    private BigDecimal price;

    @Min(value = 0, message = "Thời lượng không hợp lệ")
    private Integer durationHours;
}
