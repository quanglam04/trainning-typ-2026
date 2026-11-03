package com.example.coursemanagement_finalprojectmodule03.security.jwt;

import com.example.coursemanagement_finalprojectmodule03.model.repo.IRevokedTokenRepo;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;
    private final IRevokedTokenRepo revokedTokenRepo;

    public String getTokenFromRequest(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return null;
        }
        return header.substring(7);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getTokenFromRequest(request);

            if (token != null) {
                // 1. Kiểm tra Token có trong Blacklist (đã logout) không?
                if (revokedTokenRepo.existsByToken(token)) {
                    log.warn("Token nằm trong danh sách đã bị thu hồi (Blacklist): {}", token);
                    request.setAttribute("exception", new BadCredentialsException("Token đã bị đăng xuất/thu hồi!"));
                }
                // 2. Validate cú pháp và hạn Token
                else if (jwtProvider.validateToken(token)) {

                    if (!jwtProvider.isAccessToken(token)) {
                        log.warn("Token không phải là Access Token: {}", token);
                        request.setAttribute("exception", new BadCredentialsException("Token không hợp lệ (yêu cầu Access Token)!"));
                    } else {
                        String username = jwtProvider.extractUsername(token);
                        CustomUserDetails userDetails = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

                        // 3. Kiểm tra tài khoản có bị khóa (is_active = false) không?
                        if (!userDetails.getUser().isActive()) {
                            log.warn("Tài khoản {} đang bị khóa!", username);
                            request.setAttribute("exception", new DisabledException("Tài khoản của bạn đã bị khóa!"));
                        }
                        else {
                        // 4. So sánh thời gian tạo Token và thời gian cập nhật tài khoản gần nhất
                        Date tokenIssuedAt = jwtProvider.extractIssuedAt(token);
                        LocalDateTime userUpdatedAtLdt = userDetails.getUser().getUpdatedAt();

                        boolean isTokenExpiredByUpdate = false;
                        if (userUpdatedAtLdt != null && tokenIssuedAt != null) {
                            Date userUpdatedAt = Date.from(userUpdatedAtLdt.atZone(ZoneId.systemDefault()).toInstant());
                            // Trừ 1 giây buffer (1000ms)
                            if (tokenIssuedAt.getTime() < (userUpdatedAt.getTime() - 1000)) {
                                isTokenExpiredByUpdate = true;
                            }
                        }

                        if (isTokenExpiredByUpdate) {
                            log.warn("Token của {} đã hết hạn do thông tin tài khoản/mật khẩu vừa thay đổi!", username);
                            request.setAttribute("exception", new BadCredentialsException("Token đã hết hạn do tài khoản vừa được cập nhật!"));
                        }
                        else if (jwtProvider.validateToken(token, userDetails)) {
                            // TẤT CẢ ĐIỀU KIỆN HỢP LỆ -> Nạp thông tin vào Security Context
                            Authentication authentication = new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        }
                    }
                }
            }
        }
    }
        // Bắt các ngoại lệ của JWT và BẮT BUỘC gán vào Request attribute
        catch (ExpiredJwtException | MalformedJwtException | SignatureException | UnsupportedJwtException | IllegalArgumentException e) {
            log.error("Lỗi JWT Token: {}", e.getMessage());
            request.setAttribute("exception", e);
        }
        catch (Exception e) {
            log.error("Lỗi xác thực Token không lường trước: {}", e.getMessage());
            request.setAttribute("exception", e);
        }

        // DUY NHẤT 1 lần gọi filterChain ở cuối hàm!
        filterChain.doFilter(request, response);
    }
}