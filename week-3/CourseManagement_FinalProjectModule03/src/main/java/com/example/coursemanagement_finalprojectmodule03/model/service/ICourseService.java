package com.example.coursemanagement_finalprojectmodule03.model.service;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.CourseCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.CourseUpdateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.CourseUpdateStatusRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.CourseDetailResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.CourseResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.CourseListResponse;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Course;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICourseService {
    Page<CourseListResponse> getAllCourses(String keyword, Integer teacherId, String status, CustomUserDetails currentUser, Pageable pageable);

    CourseDetailResponse getCourseById(Integer courseId, CustomUserDetails currentUser);

    CourseResponse createCourse(CourseCreateRequest request);

    CourseResponse updateCourse(Integer courseId, CourseUpdateRequest request);

    CourseResponse updateCourseStatus(Integer courseId, CourseUpdateStatusRequest request);

    void deleteCourse(Integer courseId);

    void checkOwnership(Course course, CustomUserDetails currentUser);

}
