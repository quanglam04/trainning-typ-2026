package com.example.coursemanagement_finalprojectmodule03.model.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class AuthRefreshAccessTokenResponse {
    private String accessToken;
}
