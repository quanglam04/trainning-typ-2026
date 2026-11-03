package com.example.coursemanagement_finalprojectmodule03.model.service.impl;

import com.example.coursemanagement_finalprojectmodule03.constant.Role;
import com.example.coursemanagement_finalprojectmodule03.exception.AuthException;
import com.example.coursemanagement_finalprojectmodule03.exception.BadRequestException;
import com.example.coursemanagement_finalprojectmodule03.exception.ConflictException;
import com.example.coursemanagement_finalprojectmodule03.exception.ResourceNotFoundException;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.*;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.UserProfileResponse;
import com.example.coursemanagement_finalprojectmodule03.model.entity.User;
import com.example.coursemanagement_finalprojectmodule03.model.mapper.UserMapper;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IUserRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.spec.UserSpecification;
import com.example.coursemanagement_finalprojectmodule03.model.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {

    private final IUserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public Page<UserProfileResponse> getAllUsers(Role role, Boolean status, Pageable pageable) {
        Specification<User> spec = UserSpecification.filterUsers(role, status);
        return userRepo.findAll(spec, pageable).map(userMapper::toResponse);
    }

    @Override
    public UserProfileResponse getUserById(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));
        return userMapper.toResponse(user);
    }

    @Transactional
    @Override
    public UserProfileResponse createUser(UserCreateRequest request) {
        if (userRepo.existsByUsername(request.getUsername())) {
            throw new ConflictException("Tên đăng nhập đã tồn tại!");
        }
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã được sử dụng!");
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .isActive(true)
                .build();

        User savedUser = userRepo.save(newUser);
        return userMapper.toResponse(savedUser);
    }

    @Transactional
    @Override
    public void updateUserRole(Integer targetUserId, UserUpdateRoleRequest request, Integer currentUserId) {
        User targetUser = userRepo.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng!"));

        // RÀNG BUỘC NGHIÊM NGẶT CỦA SRS: ADMIN không được sửa ROLE của ADMIN khác!
        if (targetUser.getRole() == Role.ADMIN) {
            throw new AccessDeniedException("Tuyệt đối không được phép thay đổi hoặc hạ quyền của tài khoản Quản trị viên!");
        }

        targetUser.setRole(request.getRole());
        userRepo.save(targetUser);
    }

    @Transactional
    @Override
    public void updateUserStatus(Integer userId, UserUpdateStatusRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng!"));
        user.setActive(request.getIsActive());
        userRepo.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(Integer userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng!"));
        user.setDeleted(true);
        userRepo.save(user);
    }

    @Transactional
    @Override
    public UserProfileResponse updateProfile(Integer userId, UserUpdateRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng!"));

        if (request.getEmail() != null) {
            String newEmail = request.getEmail().trim();
            if (newEmail.isEmpty()) {
                request.setEmail(null);
            } else {
                if (!user.getEmail().equalsIgnoreCase(newEmail) && userRepo.existsByEmail(newEmail)) {
                    throw new ConflictException("Email này đã được sử dụng bởi tài khoản khác!");
                }
                request.setEmail(newEmail);
            }
        }

        if (request.getFullName() != null && request.getFullName().trim().isEmpty()) {
            request.setFullName(null);
        }

        userMapper.updateUserFromDto(request, user);
        User savedUser = userRepo.save(user);
        return userMapper.toResponse(savedUser);
    }

    @Transactional
    @Override
    public void changePassword(Integer userId, UserChangePasswordRequest request) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng!"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new AuthException("Mật khẩu cũ không chính xác!");
        }
        if (!Objects.equals(request.getNewPassword(), request.getConfirmPassword())) {
            throw new BadRequestException("Mật khẩu xác nhận không khớp!");
        }
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);
    }
}
