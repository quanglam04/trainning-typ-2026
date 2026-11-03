package com.example.coursemanagement_finalprojectmodule03.cache;

import com.example.coursemanagement_finalprojectmodule03.model.dto.res.CourseDetailResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseDetailCache {

    private static final String PREFIX = "week5:course:detail:";
    private static final long TTL_SECONDS = 120;

    private final StringRedisTemplate redis;
    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    public Optional<CourseDetailResponse> get(Integer courseId) {
        try {
            String json = redis.opsForValue().get(key(courseId));
            if (json == null) return Optional.empty();
            CourseDetailResponse data = mapper.readValue(json, CourseDetailResponse.class);
            // Phòng trường hợp key bị ghi nhầm hoặc dữ liệu cache cũ không hợp lệ.
            if (!"PUBLISHED".equals(data.getStatus())) return Optional.empty();
            return Optional.of(data);
        } catch (Exception e) {
            log.warn("Redis cache read failed for course {}: {}", courseId, e.getMessage());
            return Optional.empty(); // Redis lỗi vẫn có thể đọc từ PostgreSQL.
        }
    }

    public void put(CourseDetailResponse data) {
        if (data == null || !"PUBLISHED".equals(data.getStatus())) return;
        try {
            String json = mapper.writeValueAsString(data);
            long seconds = TTL_SECONDS + ThreadLocalRandom.current().nextLong(0, 31);
            redis.opsForValue().set(key(data.getCourseId()), json, Duration.ofSeconds(seconds));
        } catch (Exception e) {
            log.warn("Redis cache write failed for course {}: {}", data.getCourseId(), e.getMessage());
        }
    }

    public void evict(Integer courseId) {
        try {
            redis.delete(key(courseId));
        } catch (Exception e) {
            log.warn("Redis cache eviction failed for course {}: {}", courseId, e.getMessage());
        }
    }

    private String key(Integer courseId) {
        return PREFIX + courseId;
    }
}
