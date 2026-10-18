package com.example.coursemanagement_finalprojectmodule03.model.service.impl;

import com.example.coursemanagement_finalprojectmodule03.constant.CourseStatus;
import com.example.coursemanagement_finalprojectmodule03.constant.Role;
import com.example.coursemanagement_finalprojectmodule03.exception.BadRequestException;
import com.example.coursemanagement_finalprojectmodule03.exception.ConflictException;
import com.example.coursemanagement_finalprojectmodule03.exception.ResourceNotFoundException;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.LessonCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.LessonPublishRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.LessonUpdateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.LessonListResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.LessonPreviewResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.LessonResponse;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Course;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Lesson;
import com.example.coursemanagement_finalprojectmodule03.model.mapper.LessonMapper;
import com.example.coursemanagement_finalprojectmodule03.model.repo.ICourseRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.ILessonRepo;
import com.example.coursemanagement_finalprojectmodule03.model.service.ILessonService;
import com.example.coursemanagement_finalprojectmodule03.model.service.thirdparty.UploadFileService;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements ILessonService {

    private final ILessonRepo lessonRepo;
    private final ICourseRepo courseRepo;
    private final LessonMapper lessonMapper;
    private final UploadFileService uploadFileService;

    private void validateCourseOwnership(Course course, CustomUserDetails currentUser) {
        if (currentUser.getUser().getRole() == Role.ADMIN) return;

        if (currentUser.getUser().getRole() == Role.TEACHER) {
            if (!course.getTeacher().getId().equals(currentUser.getUser().getId())) {
                throw new AccessDeniedException("Bạn không có quyền thao tác trên khóa học của giảng viên khác!");
            }
        } else {
            throw new AccessDeniedException("Chỉ Giảng viên phụ trách hoặc Admin mới có quyền thực hiện!");
        }
    }

    // API 16: Danh sách bài học của Khóa học
    @Override
    public List<LessonListResponse> getLessonsByCourse(Integer courseId, CustomUserDetails currentUser) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học!"));

        List<Lesson> lessons;

        if (currentUser != null && (currentUser.getUser().getRole() == Role.ADMIN ||
                (currentUser.getUser().getRole() == Role.TEACHER && course.getTeacher().getId().equals(currentUser.getUser().getId())))) {
            lessons = lessonRepo.findByCourseIdOrderByOrderIndexAsc(courseId);
        } else {
            lessons = lessonRepo.findByCourseIdAndIsPublishedTrueOrderByOrderIndexAsc(courseId);
        }

        return lessons.stream()
                .map(lessonMapper::toListResponse)
                .collect(Collectors.toList());
    }

    // API 17: Chi tiết một bài học
    @Override
    public LessonResponse getLessonById(Integer lessonId, CustomUserDetails currentUser) {
        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học!"));

        if (!lesson.isPublished()) {
            validateCourseOwnership(lesson.getCourse(), currentUser);
        }

        return lessonMapper.toResponse(lesson);
    }

    // API 18: Thêm bài học mới
    @Transactional
    @Override
    public LessonResponse createLesson(Integer courseId, LessonCreateRequest request, CustomUserDetails currentUser) {
        // 1. Kiểm tra khóa học tồn tại
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khóa học!"));

        // 2. Chặn quyền sở hữu (Chỉ giảng viên phụ trách hoặc Admin)
        validateCourseOwnership(course, currentUser);

        // 3. Chặn trạng thái khóa học đã lưu trữ (ARCHIVED)
        if (course.getStatus() == CourseStatus.ARCHIVED) {
            throw new BadRequestException("Không thể thêm bài học vào khóa học đã lưu trữ (ARCHIVED)!");
        }

        // 4. CHẶN TRÙNG THỨ TỰ BÀI HỌC: Không cho phép 2 bài cùng orderIndex trong 1 course
        if (lessonRepo.existsByCourseIdAndOrderIndex(courseId, request.getOrderIndex())) {
            throw new ConflictException("Thứ tự bài học (Order Index) " + request.getOrderIndex() + " đã tồn tại trong khóa học này!");
        }

        // 5. CHẶN BÀI HỌC RỖNG: Phải có ít nhất File (Video/PDF) hoặc TextContent
        boolean hasFile = request.getFile() != null && !request.getFile().isEmpty();
        boolean hasText = request.getTextContent() != null && !request.getTextContent().trim().isEmpty();
        if (!hasFile && !hasText) {
            throw new BadRequestException("Bài học phải có ít nhất nội dung văn bản hoặc tệp đính kèm (Video/PDF)!");
        }

        // 6. Xử lý Upload file lên Cloudinary nếu có
        String contentUrl = null;
        if (hasFile) {
            String folder = "Module03_CourseManagement/courses/course_" + courseId + "/lessons";
            String publicId = "lesson_" + request.getOrderIndex() + "_" + System.currentTimeMillis();
            contentUrl = uploadFileService.uploadFileToCloudinary(request.getFile(),folder,publicId);
        }

        // 7. Tạo Entity Lesson mới (textContent được trim sạch khoảng trắng thừa nếu có)
        Lesson newLesson = Lesson.builder()
                .course(course)
                .title(request.getTitle().trim())
                .contentUrl(contentUrl)
                .textContent(hasText ? request.getTextContent().trim() : null)
                .orderIndex(request.getOrderIndex())
                .build();

        return lessonMapper.toResponse(lessonRepo.save(newLesson));
    }

    // API 19: Cập nhật bài học
    @Transactional
    @Override
    public LessonResponse updateLesson(Integer lessonId, LessonUpdateRequest request, CustomUserDetails currentUser) {
        // 1. Kiểm tra bài học tồn tại
        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học!"));

        Course course = lesson.getCourse();

        // 2. Chặn quyền thao tác (Chỉ Admin hoặc Giảng viên sở hữu khóa học)
        validateCourseOwnership(course, currentUser);

        // 3. Chặn cập nhật bài học nếu khóa học đã bị ARCHIVED
        if (course.getStatus() == CourseStatus.ARCHIVED) {
            throw new BadRequestException("Không thể chỉnh sửa bài học của khóa học đã lưu trữ (ARCHIVED)!");
        }

        // 4. CHẶN TRÙNG THỨ TỰ: Nếu có đổi orderIndex, kiểm tra xem số mới đã bị bài khác chiếm chưa
        if (request.getOrderIndex() != null && !request.getOrderIndex().equals(lesson.getOrderIndex())) {
            if (lessonRepo.existsByCourseIdAndOrderIndex(course.getId(), request.getOrderIndex())) {
                throw new ConflictException("Thứ tự bài học (Order Index) " + request.getOrderIndex() + " đã tồn tại trong khóa học này!");
            }
        }

        // 5. Cập nhật các trường văn bản từ DTO (bỏ qua các trường null)
        lessonMapper.updateLessonFromDto(request, lesson);

        // 6. Nếu có gửi kèm file mới -> Upload lên Cloudinary và ghi đè contentUrl
        if (request.getFile() != null && !request.getFile().isEmpty()) {
            String folder = "Module03_CourseManagement/courses/course_" + course.getId() + "/lessons";
            String publicId = "lesson_" + lesson.getOrderIndex() + "_" + System.currentTimeMillis();
            String newUrl = uploadFileService.uploadFileToCloudinary(request.getFile(), folder, publicId);
            lesson.setContentUrl(newUrl);
        }

        return lessonMapper.toResponse(lessonRepo.saveAndFlush(lesson));
    }

    // API 20: Bật/Tắt trạng thái hiển thị
    @Transactional
    @Override
    public LessonResponse updateLessonPublishStatus(Integer lessonId, LessonPublishRequest request, CustomUserDetails currentUser) {
        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học!"));

        validateCourseOwnership(lesson.getCourse(), currentUser);

        lesson.setPublished(request.getIsPublished());

        return lessonMapper.toResponse(lessonRepo.saveAndFlush(lesson));
    }

    // API 21: Xóa mềm bài học
    @Transactional
    @Override
    public void deleteLesson(Integer lessonId, CustomUserDetails currentUser) {
        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học!"));

        validateCourseOwnership(lesson.getCourse(), currentUser);

        lesson.setDeleted(true);
        lessonRepo.save(lesson);
    }

    // API 44: Xem trước nội dung
    @Override
    public LessonPreviewResponse getContentPreview(Integer lessonId, CustomUserDetails currentUser) {
        Lesson lesson = lessonRepo.findById(lessonId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy bài học!"));

        String previewText = null;
        if (lesson.getTextContent() != null && !lesson.getTextContent().trim().isEmpty()) {
            String text = lesson.getTextContent().trim();
            previewText = (text.length() > 200) ? text.substring(0, 200) + "..." : text;
        }

        return LessonPreviewResponse.builder()
                .lessonId(lesson.getId())
                .title(lesson.getTitle())
                .textContent(previewText)
                .build();
    }
}