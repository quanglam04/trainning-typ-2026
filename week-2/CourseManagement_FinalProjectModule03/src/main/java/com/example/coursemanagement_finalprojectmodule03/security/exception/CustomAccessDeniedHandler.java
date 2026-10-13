package com.example.coursemanagement_finalprojectmodule03.security.exception;
import com.example.coursemanagement_finalprojectmodule03.model.dto.wrapper.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(@NonNull HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.error("Access Denied: {}", accessDeniedException.getMessage());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(false)
                .statusCode(403)
                .errorCode("ACCESS_DENIED")
                .message("Bạn không có quyền truy cập tài nguyên này!")
                .build();

        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(response.getOutputStream(), apiResponse);
    }
}