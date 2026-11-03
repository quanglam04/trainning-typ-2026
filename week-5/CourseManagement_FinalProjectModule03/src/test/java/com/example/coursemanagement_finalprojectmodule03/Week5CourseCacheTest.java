package com.example.coursemanagement_finalprojectmodule03;

import com.example.coursemanagement_finalprojectmodule03.cache.CourseDetailCache;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.CourseDetailResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.LessonResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class Week5CourseCacheTest {
    private StringRedisTemplate redis;
    private ValueOperations<String, String> values;
    private CourseDetailCache cache;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setup() {
        redis = mock(StringRedisTemplate.class);
        values = mock(ValueOperations.class);
        when(redis.opsForValue()).thenReturn(values);
        cache = new CourseDetailCache(redis);
    }

    @Test
    void cacheMissGivesEmpty() {
        when(values.get("week5:course:detail:4")).thenReturn(null);
        assertTrue(cache.get(4).isEmpty());
    }

    @Test
    void publishedDetailCanBeSerializedAndReadBack() {
        CourseDetailResponse course = CourseDetailResponse.builder()
                .courseId(4)
                .status("PUBLISHED")
                .title("Java")
                .createdAt(LocalDateTime.of(2026, 11, 3, 8, 0))
                .lessons(List.of(LessonResponse.builder()
                        .lessonId(1).title("Bai 1").isPublished(true)
                        .createdAt(LocalDateTime.of(2026, 11, 3, 9, 0)).build()))
                .build();
        final String[] json = {null};
        doAnswer(invocation -> {
            json[0] = invocation.getArgument(1);
            Duration ttl = invocation.getArgument(2);
            assertTrue(ttl.toSeconds() >= 120 && ttl.toSeconds() <= 150);
            return null;
        }).when(values).set(eq("week5:course:detail:4"), anyString(), any(Duration.class));
        cache.put(course);
        assertNotNull(json[0]);
        when(values.get("week5:course:detail:4")).thenReturn(json[0]);
        CourseDetailResponse loaded = cache.get(4).orElseThrow();
        assertEquals("Java", loaded.getTitle());
        assertEquals(course.getCreatedAt(), loaded.getCreatedAt());
        assertEquals(1, loaded.getLessons().size());
    }

    @Test
    void draftMustNotBeCached() {
        cache.put(CourseDetailResponse.builder().courseId(9).status("DRAFT").build());
        verify(values, never()).set(anyString(), anyString(), any(Duration.class));
    }

    @Test
    void redisErrorFallsBackToDatabasePath() {
        when(values.get(anyString())).thenThrow(new IllegalStateException("Redis down"));
        assertTrue(cache.get(4).isEmpty());
    }

    @Test
    void evictionDeletesMatchingKey() {
        cache.evict(4);
        verify(redis).delete("week5:course:detail:4");
    }
}
