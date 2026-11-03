package com.example.coursemanagement_finalprojectmodule03.model.service.impl;

import com.example.coursemanagement_finalprojectmodule03.constant.Role;
import com.example.coursemanagement_finalprojectmodule03.exception.AuthException;
import com.example.coursemanagement_finalprojectmodule03.exception.BadRequestException;
import com.example.coursemanagement_finalprojectmodule03.exception.ResourceNotFoundException;
import com.example.coursemanagement_finalprojectmodule03.model.dto.req.*;
import com.example.coursemanagement_finalprojectmodule03.exception.ConflictException;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.AuthProfileResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.AuthRefreshAccessTokenResponse;
import com.example.coursemanagement_finalprojectmodule03.model.dto.res.AuthLoginResponse;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Otp;
import com.example.coursemanagement_finalprojectmodule03.model.entity.RevokedToken;
import com.example.coursemanagement_finalprojectmodule03.model.entity.User;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IOtpRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IRevokedTokenRepo;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IUserRepo;
import com.example.coursemanagement_finalprojectmodule03.model.service.IAuthService;
import com.example.coursemanagement_finalprojectmodule03.model.service.thirdparty.MailService;
import com.example.coursemanagement_finalprojectmodule03.security.jwt.JwtProvider;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final IUserRepo userRepo;
    private final IOtpRepo otpRepo;
    private final IRevokedTokenRepo revokedTokenRepo; // Dùng cho API Verify và Logout
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final MailService mailService;

    private String generateOtpCode() {
        Random random = new Random();
        int otpNumber = random.nextInt(900000) + 100000;
        return String.valueOf(otpNumber);
    }

    private void generateAndSendOtp(User user) {
        String otpCode = generateOtpCode();

        Otp otp = otpRepo.findByUser(user).orElse(new Otp());
        otp.setUser(user);
        otp.setOtpCode(otpCode);
        otp.setExpiresAt(LocalDateTime.now().plusMinutes(5));
        otp.setUsed(false);

        otpRepo.save(otp);

        // Gọi Service gửi mail
        mailService.sendMail(user.getEmail(), otpCode);
    }

    @Override
    @Transactional
    public void register(AuthRegisterRequest request) {
        if (userRepo.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã được sử dụng!");
        }
        if (userRepo.existsByUsername(request.getUsername())) {
            throw new ConflictException("Tên đăng nhập đã tồn tại!");
        }

        User newUser = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .fullName(request.getFullName())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(Role.STUDENT)
                .build();
        User savedUser = userRepo.save(newUser);

        // Sinh OTP và lưu vào DB
        generateAndSendOtp(savedUser);
    }

    @Override
    @Transactional
    public String activeUser(AuthActiveRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản có EMAIL " +request.getEmail()+" không tồn tại!"));

        if (user.isActive()) {
            throw new BadRequestException("Tài khoản này đã được kích hoạt trước đó!");
        }

        Otp otp = validateAndGetOtp(user, request.getOtp());

        user.setActive(true);
        userRepo.save(user);

        otp.setUsed(true);
        otpRepo.save(otp);

        return "Kích hoạt tài khoản thành công!";
    }

    @Override
    public AuthLoginResponse login(AuthLoginRequest request) {
        User existingUser = userRepo.findByUsername(request.getUsername())
                .orElseThrow(() -> new AuthException("Tên đăng nhập hoặc mật khẩu không chính xác"));

        if (!existingUser.isActive()) {
            if (existingUser.getRole() == Role.STUDENT) {
                // Với Sinh viên: Có thể do chưa nhập OTP, hoặc do Admin khóa. Trả câu thông báo chung.
                throw new AuthException("Tài khoản chưa được kích hoạt hoặc đã bị Quản trị viên khóa!");
            } else {
                // Với Giảng viên/Admin: Chắc chắn là bị khóa
                throw new AuthException("Tài khoản của bạn đã bị vô hiệu hóa bởi Quản trị viên!");
            }
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new AuthException("Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        assert userDetails != null;
        String accessToken = jwtProvider.generateToken(userDetails);
        String refreshToken = jwtProvider.generateRefreshToken(userDetails);

        return AuthLoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .email(existingUser.getEmail())
                .username(existingUser.getUsername())
                .tokenType("Bearer")
                .role(existingUser.getRole().name())
                .build();
    }

    @Override
    public AuthRefreshAccessTokenResponse refreshToken(AuthRefreshTokenRequest request) {
        String refreshToken = request.getRefreshToken();

        String username;
        try {
            username = jwtProvider.extractUsername(refreshToken);
        } catch (Exception e) {
            throw new BadRequestException("Refresh Token không hợp lệ");
        }

        if (revokedTokenRepo.existsByToken(refreshToken)) {
            throw new AuthException("Token này đã bị thu hồi, vui lòng đăng nhập lại!");
        }

        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại"));

        if (!user.isActive()) {
            throw new AuthException("Tài khoản đã bị khóa!");
        }

        CustomUserDetails userDetails = new CustomUserDetails(user);

        if (!jwtProvider.validateToken(refreshToken, userDetails)) {
            throw new BadRequestException("Refresh Token đã hết hạn hoặc không hợp lệ");
        }

        String newAccessToken = jwtProvider.generateToken(userDetails);

        return AuthRefreshAccessTokenResponse.builder()
                .accessToken(newAccessToken)
                .build();
    }

    @Override
    public void verifyToken(CustomUserDetails currentUser) {
        if (currentUser == null || !currentUser.getUser().isActive()) {
            throw new AuthException("Tài khoản không tồn tại hoặc đã bị vô hiệu hóa!");
        }
    }

    @Override
    public void logout(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Không tìm thấy Token hợp lệ để đăng xuất");
        }

        String token = authHeader.substring(7);

        try {
            // Nếu token chưa hết hạn, lôi ra và ném vào danh sách đen
            Date expirationDate = jwtProvider.extractExpiration(token);
            LocalDateTime expiresAt = expirationDate.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            if (!revokedTokenRepo.existsByToken(token)) {
                RevokedToken revokedToken = RevokedToken.builder()
                        .token(token)
                        .expiresAt(expiresAt)
                        .build();
                revokedTokenRepo.save(revokedToken);
            }
        } catch (Exception e) {
            // Nếu token đã hết hạn hoặc hỏng, kệ nó, đằng nào cũng không xài được nữa
            // Không làm gì cả, chỉ việc clear context ở dưới là xong
        }

        SecurityContextHolder.clearContext();
    }

    @Override
    public AuthProfileResponse getMe(CustomUserDetails userDetails) {
        Integer currentUserId = userDetails.getUser().getId();
        User user = userRepo.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản không tồn tại!"));
        return AuthProfileResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Transactional
    @Override
    public void forgotPassword(AuthForgotPasswordRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Email không tồn tại trong hệ thống!"));

        if (!user.isActive()) {
            throw new BadRequestException("Tài khoản chưa được kích hoạt, không thể lấy lại mật khẩu!");
        }
        generateAndSendOtp(user);
    }

    @Transactional
    @Override
    public void resetPassword(AuthResetPasswordRequest request) {
        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Tài khoản có EMAIL "+request.getEmail()+" không tồn tại!"));

        Otp otp = validateAndGetOtp(user, request.getOtp());

        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepo.save(user);

        otp.setUsed(true);
        otpRepo.save(otp);
    }

    private Otp validateAndGetOtp(User user, String inputOtpCode) {
        Otp otp = otpRepo.findByUser(user)
                .orElseThrow(() -> new BadRequestException("Không tìm thấy mã OTP cho tài khoản này!"));

        if (otp.isUsed()) {
            throw new BadRequestException("Mã OTP này đã được sử dụng!");
        }
        if (otp.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Mã OTP đã hết hạn, vui lòng yêu cầu mã mới!");
        }
        if (!otp.getOtpCode().equals(inputOtpCode)) {
            throw new BadRequestException("Mã OTP không chính xác!");
        }
        return otp;
    }
}
