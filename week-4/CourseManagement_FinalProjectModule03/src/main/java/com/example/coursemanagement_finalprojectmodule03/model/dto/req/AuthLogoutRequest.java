package com.example.coursemanagement_finalprojectmodule03.model.dto.req;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthLogoutRequest {
    @NotBlank(message = "Refresh Token không được để trống")
    private String refreshToken;
}
