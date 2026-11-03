package com.example.coursemanagement_finalprojectmodule03.model.service.impl;

import com.example.coursemanagement_finalprojectmodule03.constant.CourseStatus;
import com.example.coursemanagement_finalprojectmodule03.constant.Role;
import com.example.coursemanagement_finalprojectmodule03.exception.BadRequestException;
import com.example.coursemanagement_finalprojectmodule03.exception.ConflictException;
import com.example.coursemanagement_finalprojectmodule03.exception.ResourceNotFoundException;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.CourseCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.CourseUpdateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.CourseUpdateStatusRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.CourseDetailResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.CourseListResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.CourseResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.LessonResponse;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Course;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Lesson;
import com.example.coursemanagement_finalprojectmodule03.model.entity.User;
import com.example.coursemanagement_finalprojectmodule03.model.mapper.CourseMapper;
import com.example.coursemanagement_finalprojectmodule03.model.mapper.LessonMapper;
import com.example.coursemanagement_finalprojectmodule03.model.repo.*;
import com.example.coursemanagement_finalprojectmodule03.model.repo.spec.CourseSpecification;
import com.example.coursemanagement_finalprojectmodule03.model.service.ICourseService;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class CourseServiceImpl implements ICourseService {

    private final ICourseRepo courseRepo;
    private final IUserRepo userRepo;
    private final ILessonRepo lessonRepo;
    private final CourseMapper courseMapper;
    private final LessonMapper lessonMapper;
    private final IReviewRepo reviewRepo;
    private final IEnrollmentRepo enrollmentRepo;


    @Override
    public Page<CourseListResponse> getAllCourses(String keyword, Integer teacherId, String status, CustomUserDetails currentUser, Pageable pageable) {
        Specification<Course> spec = CourseSpecification.filterCourses(keyword, teacherId, status, currentUser);
        Page<Course> courses = courseRepo.findAll(spec, pageable);
        return courses.map(courseMapper::toListResponse);
    }

    @Override
    public CourseDetailResponse getCourseById(Integer courseId, CustomUserDetails currentUser) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học!"));

        if (course.getStatus() != CourseStatus.PUBLISHED) {
            boolean isAdmin = currentUser != null && currentUser.getUser().getRole() == Role.ADMIN;
            boolean isOwnerTeacher = currentUser != null
                    && currentUser.getUser().getRole() == Role.TEACHER
                    && course.getTeacher().getId().equals(currentUser.getUser().getId());

            if (!isAdmin && !isOwnerTeacher) {
                throw new ResourceNotFoundException("Không tìm thấy khóa học!");
            }
        }

        List<Lesson> publishedLessons = lessonRepo.findByCourseIdAndIsPublishedTrueOrderByOrderIndexAsc(courseId);
        List<LessonResponse> lessonResponses = publishedLessons.stream()
                .map(lessonMapper::toResponse)
                .collect(Collectors.toList());

        CourseDetailResponse response = courseMapper.toDetailResponse(course, lessonResponses);

        // NHÉT ĐIỂM VÀO CHI TIẾT KHÓA HỌC
        Double avgRating = reviewRepo.calculateAverageRatingByCourseId(courseId);
        Long totalReviews = reviewRepo.countByCourseId(courseId);
        response.setAverageRating(avgRating != null ? avgRating : 0.0);
        response.setTotalReviews(totalReviews);

        return response;
    }

    @Transactional
    @Override
    public CourseResponse createCourse(CourseCreateRequest request) {
        User teacher = userRepo.findById(request.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Giảng viên!"));

        if (teacher.getRole() != Role.TEACHER) {
            throw new BadRequestException("Người dùng được gán không phải là Giảng viên!");
        }

        Course course = Course.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .teacher(teacher)
                .price(request.getPrice() != null ? request.getPrice() : BigDecimal.ZERO)
                .durationHours(request.getDurationHours())
                .build();

        CourseResponse response = courseMapper.toResponse(courseRepo.save(course));
        enrichReviewStats(response, response.getCourseId());
        return response;

    }

    @Transactional
    @Override
    public CourseResponse updateCourse(Integer courseId, CourseUpdateRequest request) {
        if (request.getTitle() == null
                && request.getDescription() == null
                && request.getTeacherId() == null
                && request.getPrice() == null
                && request.getDurationHours() == null) {
            throw new BadRequestException("Cần gửi ít nhất một trường cần cập nhật.");
        }

        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học!"));

        if (request.getTeacherId() != null) {
            User teacher = userRepo.findById(request.getTeacherId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Giảng viên!"));

            if (teacher.getRole() != Role.TEACHER) {
                throw new BadRequestException("Người dùng được gán không phải là Giảng viên!");
            }
            course.setTeacher(teacher);
        }

        courseMapper.updateCourseFromDto(request, course);

        CourseResponse response = courseMapper.toResponse(courseRepo.saveAndFlush(course));
        enrichReviewStats(response, response.getCourseId());
        return response;
    }

    @Transactional
    @Override
    public CourseResponse updateCourseStatus(Integer courseId, CourseUpdateStatusRequest request) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học!"));

        course.setStatus(request.getStatus());

        CourseResponse response = courseMapper.toResponse(courseRepo.saveAndFlush(course));
        enrichReviewStats(response, response.getCourseId());
        return response;
    }

    @Transactional
    @Override
    public void deleteCourse(Integer courseId) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học!"));

        if (enrollmentRepo.existsByCourseId(courseId)) {
            throw new ConflictException("Không thể xóa! Khóa học đã có sinh viên đăng ký. Hãy chuyển trạng thái sang ARCHIVED.");
        }
        course.setDeleted(true);
        courseRepo.save(course);
    }

    @Override
    public void checkOwnership(Course course, CustomUserDetails currentUser) {
        if (currentUser.getUser().getRole() == Role.ADMIN) return;
        if (currentUser.getUser().getRole() == Role.TEACHER) {
            if (!course.getTeacher().getId().equals(currentUser.getUser().getId())) {
                throw new AccessDeniedException("Bạn không có quyền thao tác trên khóa học của giảng viên khác!");
            }
        } else {
            throw new AccessDeniedException("Bạn không có quyền thực hiện chức năng này!");
        }
    }

    private void enrichReviewStats(CourseResponse response, Integer courseId) {
        Double avgRating = reviewRepo.calculateAverageRatingByCourseId(courseId);
        Long totalReviews = reviewRepo.countByCourseId(courseId);

        response.setAverageRating(avgRating != null ? avgRating : 0.0);
        response.setTotalReviews(totalReviews);
    }
}
