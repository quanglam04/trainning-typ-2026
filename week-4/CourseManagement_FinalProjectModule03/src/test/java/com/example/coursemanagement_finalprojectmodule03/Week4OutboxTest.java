package com.example.coursemanagement_finalprojectmodule03;

import com.example.coursemanagement_finalprojectmodule03.constant.CourseStatus;
import com.example.coursemanagement_finalprojectmodule03.constant.Role;
import com.example.coursemanagement_finalprojectmodule03.exception.ConflictException;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.EnrollmentCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Course;
import com.example.coursemanagement_finalprojectmodule03.model.entity.User;
import com.example.coursemanagement_finalprojectmodule03.model.repo.ICourseRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IUserRepo;
import com.example.coursemanagement_finalprojectmodule03.model.service.IEnrollmentService;
import com.example.coursemanagement_finalprojectmodule03.mq.OutboxEventRepository;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class Week4OutboxTest {
    @Autowired IEnrollmentService enrollmentService;
    @Autowired IUserRepo userRepo;
    @Autowired ICourseRepo courseRepo;
    @Autowired OutboxEventRepository outboxRepo;

    @Test
    @Transactional
    void enrollmentWritesOutboxInSameTransaction() {
        User teacher = userRepo.findByUsername("teacher_active").orElseThrow();
        String unique = UUID.randomUUID().toString().substring(0, 8);
        User student = userRepo.save(User.builder()
                .username("w4_" + unique)
                .email("w4_" + unique + "@example.com")
                .fullName("Week 4 test")
                .passwordHash("test_only")
                .role(Role.STUDENT)
                .isActive(true)
                .build());
        Course course = courseRepo.save(Course.builder()
                .title("Week 4 " + unique)
                .teacher(teacher)
                .price(BigDecimal.ZERO)
                .status(CourseStatus.PUBLISHED)
                .build());

        var request = new EnrollmentCreateRequest(course.getId());
        var currentUser = new CustomUserDetails(student);
        var result = enrollmentService.enrollCourse(request, currentUser);
        assertEquals(course.getId(), result.getCourseId());
        long count = outboxRepo.findAll().stream()
                .filter(e -> e.getStudentId().equals(student.getId()))
                .filter(e -> e.getCourseId().equals(course.getId()))
                .count();
        assertEquals(1, count);
        assertThrows(ConflictException.class, () -> enrollmentService.enrollCourse(request, currentUser));
        assertEquals(1, outboxRepo.findAll().stream()
                .filter(e -> e.getStudentId().equals(student.getId()))
                .filter(e -> e.getCourseId().equals(course.getId()))
                .count());
    }
}
