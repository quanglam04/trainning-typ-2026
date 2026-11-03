package com.example.coursemanagement_finalprojectmodule03.model.service.impl;

import com.example.coursemanagement_finalprojectmodule03.constant.CourseStatus;
import com.example.coursemanagement_finalprojectmodule03.constant.EnrollmentStatus;
import com.example.coursemanagement_finalprojectmodule03.exception.BadRequestException;
import com.example.coursemanagement_finalprojectmodule03.exception.ConflictException;
import com.example.coursemanagement_finalprojectmodule03.exception.ResourceNotFoundException;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.EnrollmentCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.EnrollmentDetailResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.EnrollmentResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.LessonProgressResponse;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Course;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Enrollment;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Lesson;
import com.example.coursemanagement_finalprojectmodule03.model.entity.LessonProgress;
import com.example.coursemanagement_finalprojectmodule03.model.mapper.EnrollmentMapper;
import com.example.coursemanagement_finalprojectmodule03.model.repo.ICourseRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IEnrollmentRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.ILessonProgressRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.ILessonRepo;
import com.example.coursemanagement_finalprojectmodule03.model.service.IEnrollmentService;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EnrollmentServiceImpl implements IEnrollmentService {

    private final IEnrollmentRepo enrollmentRepo;
    private final ICourseRepo courseRepo;
    private final ILessonRepo lessonRepo;
    private final ILessonProgressRepo lessonProgressRepo;
    private final EnrollmentMapper enrollmentMapper;

    // API 22: Danh sách các khóa học sinh viên đã đăng ký
    @Override
    public Page<EnrollmentResponse> getMyEnrollments(CustomUserDetails currentUser, int page, int size) {
        Integer studentId = currentUser.getUser().getId();
        // Sắp xếp khóa học mới đăng ký lên đầu
        Pageable pageable = PageRequest.of(page, size, Sort.by("enrollmentDate").descending());

        return enrollmentRepo.findByStudentId(studentId, pageable)
                .map(enrollmentMapper::toResponse);
    }

    // API 23: Đăng ký một khóa học mới
    @Transactional
    @Override
    public EnrollmentResponse enrollCourse(EnrollmentCreateRequest request, CustomUserDetails currentUser) {
        Integer studentId = currentUser.getUser().getId();
        Integer courseId = request.getCourseId();

        // 1. Kiểm tra khóa học tồn tại
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học!"));

        // 2. Chặn đăng ký khóa học chưa mở hoặc đã đóng
        if (course.getStatus() != CourseStatus.PUBLISHED) {
            throw new BadRequestException("Chỉ có thể đăng ký các khóa học đang mở (PUBLISHED)!");
        }

        // 3. Kiểm tra lịch sử đăng ký của sinh viên
        var existingEnrollmentOpt = enrollmentRepo.findByStudentIdAndCourseId(studentId, courseId);

        if (existingEnrollmentOpt.isPresent()) {
            Enrollment existingEnrollment = existingEnrollmentOpt.get();

            if (existingEnrollment.getStatus() == EnrollmentStatus.DROPPED) {
                existingEnrollment.setStatus(EnrollmentStatus.ENROLLED);
                return enrollmentMapper.toResponse(enrollmentRepo.save(existingEnrollment));
            } else {
                throw new ConflictException("Bạn đã đăng ký khóa học này trước đó rồi!");
            }
        }

        // 4. Tạo bản ghi Enrollment mới
        Enrollment enrollment = Enrollment.builder()
                .student(currentUser.getUser())
                .course(course)
                .build();

        return enrollmentMapper.toResponse(enrollmentRepo.save(enrollment));
    }

    // API 24: Lấy chi tiết thông tin đăng ký và tiến độ học tập
    @Override
    public EnrollmentDetailResponse getEnrollmentDetail(Integer enrollmentId, CustomUserDetails currentUser) {
        Integer studentId = currentUser.getUser().getId();

        // 1. Lấy thông tin đăng ký cơ bản
        Enrollment enrollment = enrollmentRepo.findByIdAndStudentId(enrollmentId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi đăng ký khóa học hợp lệ của bạn!"));

        // 2. Lấy danh sách tất cả các bài học PUBLISHED của khóa học này
        List<Lesson> allPublishedLessons = lessonRepo.findByCourseIdAndIsPublishedTrueOrderByOrderIndexAsc(enrollment.getCourse().getId());

        // 3. Lấy danh sách ID các bài học mà sinh viên này đã hoàn thành
        // (Cần thêm hàm bổ trợ này trong ILessonProgressRepo)
        List<Integer> completedLessonIds = lessonProgressRepo.findByEnrollmentIdAndIsCompletedTrue(enrollmentId)
                .stream()
                .map(progress -> progress.getLesson().getId())
                .collect(Collectors.toList());

        // 4. Map sang DTO
        List<LessonProgressResponse> lessonProgressResponses = allPublishedLessons.stream()
                .map(lesson -> LessonProgressResponse.builder()
                        .lessonId(lesson.getId())
                        .title(lesson.getTitle())
                        .orderIndex(lesson.getOrderIndex())
                        .isCompleted(completedLessonIds.contains(lesson.getId())) // Đối chiếu xem ID đã hoàn thành chưa
                        .build())
                .collect(Collectors.toList());

        // 5. Đóng gói vào EnrollmentDetailResponse
        return EnrollmentDetailResponse.builder()
                .enrollmentId(enrollment.getId())
                .enrollmentDate(enrollment.getEnrollmentDate())
                .status(enrollment.getStatus())
                .completionDate(enrollment.getCompletionDate())
                .progressPercentage(enrollment.getProgressPercentage())
                .courseId(enrollment.getCourse().getId())
                .courseTitle(enrollment.getCourse().getTitle())
                .lessonProgressResponses(lessonProgressResponses)
                .build();
    }

    // API 25: Đánh dấu bài học đã hoàn thành & tính lại tiến độ
    @Transactional
    @Override
    public EnrollmentResponse completeLesson(Integer enrollmentId, Integer lessonId, CustomUserDetails currentUser) {
        Integer studentId = currentUser.getUser().getId();

        // 1. Kiểm tra bản ghi đăng ký hợp lệ của chính chủ sinh viên
        Enrollment enrollment = enrollmentRepo.findByIdAndStudentId(enrollmentId, studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bản ghi đăng ký khóa học!"));

        if (enrollment.getStatus() == EnrollmentStatus.DROPPED) {
            throw new BadRequestException("Không thể cập nhật tiến độ cho khóa học đã hủy (DROPPED)!");
        }

        // 2. Kiểm tra bài học tồn tại và thuộc về khóa học này
        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học!"));

        if (!lesson.getCourse().getId().equals(enrollment.getCourse().getId())) {
            throw new BadRequestException("Bài học không thuộc khóa học mà bạn đã đăng ký!");
        }

        if (!lesson.isPublished()) {
            throw new BadRequestException("Không thể hoàn thành bài học chưa được phát hành!");
        }

        // 3. Cập nhật hoặc khởi tạo bản ghi LessonProgress
        LessonProgress progress = lessonProgressRepo.findByEnrollmentIdAndLessonId(enrollmentId, lessonId)
                .orElseGet(() -> LessonProgress.builder()
                        .enrollment(enrollment)
                        .lesson(lesson)
                        .build());

        progress.setCompleted(true);
        progress.setCompletedAt(LocalDateTime.now());
        progress.setLastAccessedAt(LocalDateTime.now());
        lessonProgressRepo.save(progress);

        // 4. Tự động tính toán lại % tiến độ: (Hoàn thành PUBLISHED / Tổng bài PUBLISHED) * 100
        long totalPublishedLessons = lessonRepo.countByCourseIdAndIsPublishedTrue(enrollment.getCourse().getId());
        long completedLessonsCount = lessonProgressRepo.countCompletedPublishedLessons(enrollmentId);

        BigDecimal percentage = BigDecimal.ZERO;
        if (totalPublishedLessons > 0) {
            percentage = BigDecimal.valueOf(completedLessonsCount)
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(totalPublishedLessons), 2, RoundingMode.HALF_UP);
        }

        if (percentage.compareTo(BigDecimal.valueOf(100)) >= 0) {
            percentage = new BigDecimal("100.00");
            enrollment.setStatus(EnrollmentStatus.COMPLETED);
            enrollment.setCompletionDate(LocalDateTime.now());
        }
        enrollment.setProgressPercentage(percentage);

        return enrollmentMapper.toResponse(enrollmentRepo.saveAndFlush(enrollment));
    }
}