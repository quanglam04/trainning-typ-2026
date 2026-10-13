package com.example.coursemanagement_finalprojectmodule03.security.exception;

import com.example.coursemanagement_finalprojectmodule03.model.dto.wrapper.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class CustomAuthenticationEntryPointHandler implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Exception exception = (Exception) request.getAttribute("exception");

        String errorCode = "UNAUTHORIZED";
        String message = "Yêu cầu xác thực token trước khi truy cập!";

        if (exception instanceof ExpiredJwtException) {
            errorCode = "EXPIRED_JWT_TOKEN";
            message = "Token JWT đã hết hạn sử dụng!";
        } else if (exception instanceof MalformedJwtException || exception instanceof SignatureException || exception instanceof IllegalArgumentException) {
            errorCode = "INVALID_JWT_TOKEN";
            message = "Token JWT không hợp lệ hoặc bị can thiệp!";
        }else if (exception instanceof org.springframework.security.authentication.BadCredentialsException
                || exception instanceof org.springframework.security.authentication.DisabledException) {
            errorCode = "INVALID_JWT_TOKEN";
            message = exception.getMessage();
        } else {
            log.error("Unauthorized error: {}", authException.getMessage());
        }

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse<Void> apiResponse = ApiResponse.<Void>builder()
                .success(false)
                .statusCode(401)
                .errorCode(errorCode)
                .message(message)
                .build();

        new ObjectMapper().writerWithDefaultPrettyPrinter().writeValue(response.getOutputStream(), apiResponse);
    }
}