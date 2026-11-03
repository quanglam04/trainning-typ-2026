package com.example.coursemanagement_finalprojectmodule03.model.service;


import com.example.coursemanagement_finalprojectmodule03.constant.Role;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.*;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.UserProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IUserService {
    Page<UserProfileResponse> getAllUsers(Role role, Boolean status, Pageable pageable);

    UserProfileResponse getUserById(Integer userId);

    UserProfileResponse createUser(UserCreateRequest request);

    void updateUserRole(Integer targetUserId, UserUpdateRoleRequest request, Integer currentUserId);

    void updateUserStatus(Integer userId, UserUpdateStatusRequest request);

    void deleteUser(Integer userId);

    UserProfileResponse updateProfile(Integer userId, UserUpdateRequest request);

    void changePassword(Integer userId, UserChangePasswordRequest request);
}
