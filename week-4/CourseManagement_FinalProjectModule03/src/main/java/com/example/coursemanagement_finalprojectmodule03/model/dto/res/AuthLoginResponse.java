package com.example.coursemanagement_finalprojectmodule03.model.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuthLoginResponse {
    private String email;
    private String username;
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private String role;
}
