package com.example.coursemanagement_finalprojectmodule03.model.mapper;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.LessonUpdateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.LessonListResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.LessonResponse;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Lesson;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface LessonMapper {

    @Mapping(target = "lessonId", source = "id")
    @Mapping(target = "courseId", source = "course.id")
    LessonListResponse toListResponse(Lesson lesson);

    @Mapping(target = "lessonId", source = "id")
    @Mapping(target = "courseId", source = "course.id")
    LessonResponse toResponse(Lesson lesson);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "course", ignore = true)
    @Mapping(target = "contentUrl", ignore = true)
    @Mapping(target = "published", ignore = true)
    @Mapping(target = "deleted", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateLessonFromDto(LessonUpdateRequest dto, @MappingTarget Lesson entity);
}