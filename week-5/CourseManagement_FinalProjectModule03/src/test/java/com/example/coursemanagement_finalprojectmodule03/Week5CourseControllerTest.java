package com.example.coursemanagement_finalprojectmodule03;

import com.example.coursemanagement_finalprojectmodule03.cache.CourseDetailCache;
import com.example.coursemanagement_finalprojectmodule03.controller.CourseController;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.CourseDetailResponse;
import com.example.coursemanagement_finalprojectmodule03.model.service.ICourseService;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Week5CourseControllerTest {
    @Test
    void hitDoesNotQueryDatabase() {
        ICourseService service = mock(ICourseService.class);
        CourseDetailCache cache = mock(CourseDetailCache.class);
        var course = CourseDetailResponse.builder().courseId(3).status("PUBLISHED").build();
        when(cache.get(3)).thenReturn(Optional.of(course));

        var response = new CourseController(service, cache).getCourseById(3, true, null);

        assertEquals("HIT", response.getHeaders().getFirst("X-Cache"));
        assertSame(course, response.getBody().getData());
        verifyNoInteractions(service);
    }

    @Test
    void missReadsDatabaseAndPopulatesCache() {
        ICourseService service = mock(ICourseService.class);
        CourseDetailCache cache = mock(CourseDetailCache.class);
        var course = CourseDetailResponse.builder().courseId(3).status("PUBLISHED").build();
        when(cache.get(3)).thenReturn(Optional.empty());
        when(service.getCourseById(eq(3), isNull())).thenReturn(course);

        var response = new CourseController(service, cache).getCourseById(3, true, null);

        assertEquals("MISS", response.getHeaders().getFirst("X-Cache"));
        verify(service).getCourseById(eq(3), isNull());
        verify(cache).put(course);
    }

    @Test
    void bypassDoesNotReadOrWriteRedis() {
        ICourseService service = mock(ICourseService.class);
        CourseDetailCache cache = mock(CourseDetailCache.class);
        var course = CourseDetailResponse.builder().courseId(3).status("PUBLISHED").build();
        when(service.getCourseById(eq(3), isNull())).thenReturn(course);

        var response = new CourseController(service, cache).getCourseById(3, false, null);

        assertEquals("BYPASS", response.getHeaders().getFirst("X-Cache"));
        verifyNoInteractions(cache);
    }
}
