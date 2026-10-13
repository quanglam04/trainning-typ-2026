package com.example.coursemanagement_finalprojectmodule03.model.service;

import com.example.coursemanagement_finalprojectmodule03.model.dto.req.*;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.AuthProfileResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.AuthRefreshAccessTokenResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.AuthLoginResponse;
import com.example.coursemanagement_finalprojectmodule03.model.entity.User;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import jakarta.transaction.Transactional;

public interface IAuthService {

    @Transactional
    void register(AuthRegisterRequest authRegisterRequest);

    String activeUser(AuthActiveRequest userActiveRequest);

    AuthLoginResponse login(AuthLoginRequest authLoginRequest);

    AuthRefreshAccessTokenResponse refreshToken(AuthRefreshTokenRequest authRefreshTokenRequest);

    void verifyToken(CustomUserDetails currentUser);

    void logout(String authHeader);

    AuthProfileResponse getMe(CustomUserDetails userDetails);

    @Transactional
    void forgotPassword(AuthForgotPasswordRequest request);

    @Transactional
    void resetPassword(AuthResetPasswordRequest request);
}
