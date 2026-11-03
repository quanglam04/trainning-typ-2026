package com.example.coursemanagement_finalprojectmodule03.model.service;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.LessonCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.LessonPublishRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.LessonUpdateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.LessonListResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.LessonPreviewResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.LessonResponse;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;

import java.util.List;

public interface ILessonService {
    List<LessonListResponse> getLessonsByCourse(Integer courseId, CustomUserDetails currentUser);

    // API 17: Trả về chi tiết bài học
    LessonResponse getLessonById(Integer lessonId, CustomUserDetails currentUser);

    // API 18:
    LessonResponse createLesson(Integer courseId, LessonCreateRequest request, CustomUserDetails currentUser);

    // API 19:
    LessonResponse updateLesson(Integer lessonId, LessonUpdateRequest request, CustomUserDetails currentUser);

    // API 20: Đổi trạng thái
    LessonResponse updateLessonPublishStatus(Integer lessonId, LessonPublishRequest request, CustomUserDetails currentUser);

    // API 21: Xóa bài học
    void deleteLesson(Integer lessonId, CustomUserDetails currentUser);

    // API 44
    LessonPreviewResponse getContentPreview(Integer lessonId, CustomUserDetails currentUser);
}
