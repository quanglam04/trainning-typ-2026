package com.example.coursemanagement_finalprojectmodule03.model.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuthProfileResponse {
    private Integer userId;
    private String username;
    private String email;
    private String fullName;
    private String role;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
