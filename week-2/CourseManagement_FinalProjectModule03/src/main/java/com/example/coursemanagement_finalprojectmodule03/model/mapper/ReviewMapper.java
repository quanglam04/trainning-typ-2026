package com.example.coursemanagement_finalprojectmodule03.model.mapper;

import com.example.coursemanagement_finalprojectmodule03.model.dto.res.ReviewResponse;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "reviewId", source = "id")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "studentId", source = "student.id")
    @Mapping(target = "studentName", source = "student.fullName")
    ReviewResponse toResponse(Review review);
}