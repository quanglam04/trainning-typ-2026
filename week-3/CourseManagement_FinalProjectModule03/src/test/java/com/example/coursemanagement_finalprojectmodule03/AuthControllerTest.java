package com.example.coursemanagement_finalprojectmodule03;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.AuthLoginRequest;
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
class AuthControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("API-1: Login thành công với tài khoản hợp lệ, trả về access token và role")
    void testLogin_Success() throws Exception {
        AuthLoginRequest request = new AuthLoginRequest("admin", "123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status_code").value(200))
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }

    @Test
    @DisplayName("API-1: Login thất bại do sai mật khẩu, trả về 401 BAD_CREDENTIALS")
    void testLogin_Failure_BadCredentials() throws Exception {
        AuthLoginRequest request = new AuthLoginRequest("admin", "wrong_password");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status_code").value(401))
                .andExpect(jsonPath("$.error_code").value("BAD_CREDENTIALS"));
    }

    @Test
    @DisplayName("API-1: Login thất bại do tài khoản bị khóa, trả về 401 BAD_CREDENTIALS")
    void testLogin_Failure_LockedAccount() throws Exception {
        AuthLoginRequest request = new AuthLoginRequest("teacher_banned", "123456");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status_code").value(401))
                .andExpect(jsonPath("$.error_code").value("BAD_CREDENTIALS"));
    }

    @Test
    @DisplayName("API-3: Truy cập endpoint /api/auth/me không có JWT, trả về 401 UNAUTHORIZED")
    void testMe_Unauthorized_NoToken() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.status_code").value(401))
                .andExpect(jsonPath("$.error_code").value("UNAUTHORIZED"));
    }

    @Test
    @DisplayName("API-3: Lấy thông tin hồ sơ /api/auth/me thành công với JWT hợp lệ")
    void testMe_Success_WithToken() throws Exception {
        CustomUserDetails userDetails = userDetailsService.loadUserByUsername("admin");
        String token = jwtProvider.generateToken(userDetails);

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.status_code").value(200))
                .andExpect(jsonPath("$.data.username").value("admin"))
                .andExpect(jsonPath("$.data.role").value("ADMIN"));
    }
}
