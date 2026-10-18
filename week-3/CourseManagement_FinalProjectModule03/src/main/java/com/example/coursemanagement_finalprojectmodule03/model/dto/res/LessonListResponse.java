package com.example.coursemanagement_finalprojectmodule03.model.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LessonListResponse {
    private Integer lessonId;
    private Integer courseId;
    private String title;
    private String contentUrl;
    private Integer orderIndex;
    private Boolean isPublished;
    private LocalDateTime createdAt;
}