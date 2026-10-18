package com.example.coursemanagement_finalprojectmodule03;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.EnrollmentCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Course;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Enrollment;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Lesson;
import com.example.coursemanagement_finalprojectmodule03.model.entity.User;
import com.example.coursemanagement_finalprojectmodule03.model.repo.ICourseRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IEnrollmentRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.ILessonRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IUserRepo;
import com.example.coursemanagement_finalprojectmodule03.security.jwt.JwtProvider;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class EnrollmentAndProgressTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private IUserRepo userRepo;

    @Autowired
    private ICourseRepo courseRepo;

    @Autowired
    private ILessonRepo lessonRepo;

    @Autowired
    private IEnrollmentRepo enrollmentRepo;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    private String getTokenForUser(String username) {
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return jwtProvider.generateToken(userDetails);
    }

    @Test
    @DisplayName("Enrollment: Sinh viên đăng ký khóa học thành công (201 CREATED) và chặn đăng ký trùng (409 CONFLICT)")
    void testEnrollCourse_SuccessAndDuplicateCheck() throws Exception {
        String studentToken = getTokenForUser("student_new");
        Course coursePub = courseRepo.findByTitle("Khóa học Mở").orElseThrow();

        EnrollmentCreateRequest request = new EnrollmentCreateRequest(coursePub.getId());

        // 1. Đăng ký lần đầu -> 201 CREATED
        mockMvc.perform(post("/api/enrollments")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status_code").value(201))
                .andExpect(jsonPath("$.data.courseId").value(coursePub.getId()))
                .andExpect(jsonPath("$.data.status").value("ENROLLED"));

        // 2. Đăng ký lại cùng khóa học -> 409 CONFLICT (DUPLICATE_RESOURCE)
        mockMvc.perform(post("/api/enrollments")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status_code").value(409))
                .andExpect(jsonPath("$.error_code").value("DUPLICATE_RESOURCE"));
    }

    @Test
    @DisplayName("Progress: Tính toán tiến độ học tập chính xác và chuyển trạng thái COMPLETED khi đạt 100%")
    void testLessonCompletion_ProgressCalculation() throws Exception {
        String studentLazyToken = getTokenForUser("student_lazy");
        User studentLazy = userRepo.findByUsername("student_lazy").orElseThrow();
        Course coursePub = courseRepo.findByTitle("Khóa học Mở").orElseThrow();

        Enrollment enrollment = enrollmentRepo.findByStudentIdAndCourseId(studentLazy.getId(), coursePub.getId())
                .orElseThrow();

        List<Lesson> publishedLessons = lessonRepo.findByCourseIdAndIsPublishedTrueOrderByOrderIndexAsc(coursePub.getId());
        Lesson lesson1 = publishedLessons.get(0);
        Lesson lesson2 = publishedLessons.get(1);

        // Hoàn thành bài 1 -> Tiến độ 50%
        mockMvc.perform(put("/api/enrollments/" + enrollment.getId() + "/complete_lesson/" + lesson1.getId())
                        .header("Authorization", "Bearer " + studentLazyToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.progressPercentage").value(50.0))
                .andExpect(jsonPath("$.data.status").value("ENROLLED"));

        // Hoàn thành bài 2 -> Tiến độ 100%, trạng thái đổi sang COMPLETED
        mockMvc.perform(put("/api/enrollments/" + enrollment.getId() + "/complete_lesson/" + lesson2.getId())
                        .header("Authorization", "Bearer " + studentLazyToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.progressPercentage").value(100.0))
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }
}
