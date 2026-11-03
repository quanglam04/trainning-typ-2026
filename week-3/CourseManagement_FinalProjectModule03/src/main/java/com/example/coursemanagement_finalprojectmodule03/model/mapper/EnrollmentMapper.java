package com.example.coursemanagement_finalprojectmodule03.model.mapper;

import com.example.coursemanagement_finalprojectmodule03.model.dto.res.EnrollmentResponse;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface EnrollmentMapper {

    @Mapping(target = "enrollmentId", source = "id")
    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "courseTitle", source = "course.title")
    EnrollmentResponse toResponse(Enrollment enrollment);
}