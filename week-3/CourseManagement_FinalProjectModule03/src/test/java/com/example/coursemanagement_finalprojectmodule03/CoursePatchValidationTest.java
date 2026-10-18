package com.example.coursemanagement_finalprojectmodule03;

import com.example.coursemanagement_finalprojectmodule03.exception.BadRequestException;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.CourseUpdateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.mapper.CourseMapper;
import com.example.coursemanagement_finalprojectmodule03.model.mapper.LessonMapper;
import com.example.coursemanagement_finalprojectmodule03.model.repo.ICourseRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IEnrollmentRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.ILessonRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IReviewRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IUserRepo;
import com.example.coursemanagement_finalprojectmodule03.model.service.impl.CourseServiceImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;

class CoursePatchValidationTest {

    @Test
    void rejectsPatchWhenNoFieldsWereProvided() {
        CourseServiceImpl service = new CourseServiceImpl(
                mock(ICourseRepo.class),
                mock(IUserRepo.class),
                mock(ILessonRepo.class),
                mock(CourseMapper.class),
                mock(LessonMapper.class),
                mock(IReviewRepo.class),
                mock(IEnrollmentRepo.class)
        );

        assertThrows(BadRequestException.class,
                () -> service.updateCourse(1, new CourseUpdateRequest()));
    }
}
