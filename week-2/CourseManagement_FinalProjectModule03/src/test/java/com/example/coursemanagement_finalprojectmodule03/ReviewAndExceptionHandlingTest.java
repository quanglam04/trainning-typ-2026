package com.example.coursemanagement_finalprojectmodule03;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.ReviewCreateRequest;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Course;
import com.example.coursemanagement_finalprojectmodule03.model.repo.ICourseRepo;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ReviewAndExceptionHandlingTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

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
    @DisplayName("Validation: Điểm đánh giá > 5 hoặc để trống bị chặn bởi @Valid (400 INVALID_INPUT_DATA)")
    void testCreateReview_InvalidRating_BadRequest() throws Exception {
        String studentToken = getTokenForUser("student_pro");
        Course coursePub = courseRepo.findByTitle("Khóa học Mở").orElseThrow();

        // Rating = 6 (vượt quá Max = 5)
        ReviewCreateRequest request = new ReviewCreateRequest(6, "Nội dung hợp lệ");

        mockMvc.perform(post("/api/courses/" + coursePub.getId() + "/reviews")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status_code").value(400))
                .andExpect(jsonPath("$.error_code").value("INVALID_INPUT_DATA"));
    }

    @Test
    @DisplayName("Review: Chặn sinh viên đánh giá trùng lặp lần 2 trên cùng một khóa học (409 DUPLICATE_RESOURCE)")
    void testCreateReview_Duplicate_Conflict() throws Exception {
        String studentToken = getTokenForUser("student_pro");
        Course coursePub = courseRepo.findByTitle("Khóa học Mở").orElseThrow();

        // student_pro đã có review trong DataInitializer
        ReviewCreateRequest request = new ReviewCreateRequest(4, "Đánh giá lại lần 2");

        mockMvc.perform(post("/api/courses/" + coursePub.getId() + "/reviews")
                        .header("Authorization", "Bearer " + studentToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status_code").value(409))
                .andExpect(jsonPath("$.error_code").value("DUPLICATE_RESOURCE"));
    }

    @Test
    @DisplayName("Global Exception Handling: Truy vấn tài nguyên không tồn tại trả về 404 RESOURCE_NOT_FOUND")
    void testResourceNotFound_StandardResponse() throws Exception {
        String adminToken = getTokenForUser("admin");

        mockMvc.perform(get("/api/courses/999999")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status_code").value(404))
                .andExpect(jsonPath("$.error_code").value("RESOURCE_NOT_FOUND"))
                .andExpect(jsonPath("$.message").isNotEmpty())
                .andExpect(jsonPath("$.timestamp").isNotEmpty());
    }
}
