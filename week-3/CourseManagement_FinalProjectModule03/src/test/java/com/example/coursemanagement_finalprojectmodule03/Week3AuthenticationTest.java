package com.example.coursemanagement_finalprojectmodule03;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.AuthLogoutRequest;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.AuthRefreshTokenRequest;
import com.example.coursemanagement_finalprojectmodule03.security.jwt.JwtProvider;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class Week3AuthenticationTest {
    @Autowired private WebApplicationContext context;
    @Autowired private JwtProvider jwtProvider;
    @Autowired private CustomUserDetailsService userDetailsService;

    private final ObjectMapper mapper = new ObjectMapper();
    private MockMvc mvc;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.webAppContextSetup(context).apply(springSecurity()).build();
    }

    private CustomUserDetails user(String username) {
        return userDetailsService.loadUserByUsername(username);
    }

    private String refreshBody(String token) throws Exception {
        return mapper.writeValueAsString(new AuthRefreshTokenRequest(token));
    }

    @Test
    void refreshTokenCanIssueAccessToken() throws Exception {
        String refresh = jwtProvider.generateRefreshToken(user("admin"));
        mvc.perform(post("/api/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content(refreshBody(refresh)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty());
    }

    @Test
    void accessTokenCannotRefresh() throws Exception {
        String access = jwtProvider.generateToken(user("admin"));
        mvc.perform(post("/api/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content(refreshBody(access)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void refreshTokenCannotBeUsedForProtectedApi() throws Exception {
        String refresh = jwtProvider.generateRefreshToken(user("admin"));
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + refresh))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutRevokesAccessAndRefresh() throws Exception {
        CustomUserDetails admin = user("admin");
        String access = jwtProvider.generateToken(admin);
        String refresh = jwtProvider.generateRefreshToken(admin);
        AuthLogoutRequest request = new AuthLogoutRequest();
        request.setRefreshToken(refresh);

        mvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + access)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        mvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + access))
                .andExpect(status().isUnauthorized());
        mvc.perform(post("/api/auth/refresh").contentType(MediaType.APPLICATION_JSON)
                        .content(refreshBody(refresh)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void logoutRejectsAnotherUsersRefreshToken() throws Exception {
        String accessAdmin = jwtProvider.generateToken(user("admin"));
        AuthLogoutRequest request = new AuthLogoutRequest();
        request.setRefreshToken(jwtProvider.generateRefreshToken(user("student_pro")));

        mvc.perform(post("/api/auth/logout")
                        .header("Authorization", "Bearer " + accessAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void tokensAreDifferentAcrossSameSecond() {
        CustomUserDetails admin = user("admin");
        assertNotEquals(jwtProvider.generateToken(admin), jwtProvider.generateToken(admin));
        assertNotEquals(jwtProvider.generateRefreshToken(admin), jwtProvider.generateRefreshToken(admin));
    }
}
