package com.example.coursemanagement_finalprojectmodule03.model.mapper;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.CourseUpdateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.CourseDetailResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.CourseListResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.CourseResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.LessonResponse;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Course;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CourseMapper {

    @Mapping(target = "courseId", source = "id")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherName", source = "teacher.fullName")
    CourseListResponse toListResponse(Course entity);

    @Mapping(target = "courseId", source = "id")
    @Mapping(target = "teacherId", source = "teacher.id")
    @Mapping(target = "teacherName", source = "teacher.fullName")
    CourseResponse toResponse(Course entity);

    @Mapping(target = "courseId", source = "course.id")
    @Mapping(target = "teacherId", source = "course.teacher.id")
    @Mapping(target = "teacherName", source = "course.teacher.fullName")
    @Mapping(target = "lessons", source = "lessons")
    CourseDetailResponse toDetailResponse(Course course, List<LessonResponse> lessons);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "teacher", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateCourseFromDto(CourseUpdateRequest dto, @MappingTarget Course entity);
}