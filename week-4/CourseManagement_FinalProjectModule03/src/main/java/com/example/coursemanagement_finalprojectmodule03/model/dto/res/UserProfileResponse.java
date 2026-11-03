package com.example.coursemanagement_finalprojectmodule03.model.dto.res;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class UserProfileResponse {
    private Integer userId;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
