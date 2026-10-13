package com.example.coursemanagement_finalprojectmodule03;

import com.example.coursemanagement_finalprojectmodule03.constant.Role;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.UserUpdateRoleRequest;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Course;
import com.example.coursemanagement_finalprojectmodule03.model.entity.User;
import com.example.coursemanagement_finalprojectmodule03.model.repo.ICourseRepo;
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

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class SecurityAndRbacTest {

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
    @DisplayName("RBAC: Admin có quyền truy cập endpoint quản lý User /api/users (200 OK)")
    void testAdminAccess_Success() throws Exception {
        String adminToken = getTokenForUser("admin");

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status_code").value(200));
    }

    @Test
    @DisplayName("RBAC: Student truy cập endpoint /api/users bị từ chối quyền (403 FORBIDDEN)")
    void testStudentAccessToAdminEndpoint_Forbidden() throws Exception {
        String studentToken = getTokenForUser("student_pro");

        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + studentToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status_code").value(403))
                .andExpect(jsonPath("$.error_code").value("ACCESS_DENIED"));
    }

    @Test
    @DisplayName("Business Rule: Admin không được phép thay đổi Role của Admin khác (403 FORBIDDEN)")
    void testAdminCannotChangeRoleOfAnotherAdmin() throws Exception {
        String adminToken = getTokenForUser("admin");
        User admin2 = userRepo.findByUsername("admin_2").orElseThrow();

        UserUpdateRoleRequest request = new UserUpdateRoleRequest(Role.STUDENT);

        mockMvc.perform(put("/api/users/" + admin2.getId() + "/role")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status_code").value(403))
                .andExpect(jsonPath("$.error_code").value("ACCESS_DENIED"));
    }

    @Test
    @DisplayName("Ownership: Giảng viên không được thao tác trên bài học của khóa học thuộc giảng viên khác (403 FORBIDDEN)")
    void testTeacherCannotManageLessonsOfOtherTeacherCourse() throws Exception {
        String teacher1Token = getTokenForUser("teacher_active");
        Course courseArc = courseRepo.findByTitle("Khóa học Đã Đóng").orElseThrow();

        mockMvc.perform(multipart("/api/courses/" + courseArc.getId() + "/lessons")
                        .param("title", "Bài học xâm nhập")
                        .param("orderIndex", "99")
                        .param("textContent", "Nội dung bài học trái phép")
                        .header("Authorization", "Bearer " + teacher1Token))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status_code").value(403))
                .andExpect(jsonPath("$.error_code").value("ACCESS_DENIED"));
    }
}
