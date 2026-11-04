package com.courseshop.courseshop.service;

import java.time.Instant;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.courseshop.courseshop.model.User;
import com.courseshop.courseshop.model.UserRole;
import com.courseshop.courseshop.dto.LoginRequestDTO;
import com.courseshop.courseshop.dto.TokenResponse;
import com.courseshop.courseshop.model.RefreshToken;
import com.courseshop.courseshop.repository.RefreshTokenRepository;
import com.courseshop.courseshop.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/** Thực hiện đăng nhập qua AuthenticationManager, phát JWT và quản lý vòng đời refresh token. */
@Service
public class AuthService {

    private static final String SESSION_USERNAME = "authenticatedUsername";

    public static final long TOKEN_LIFETIME_SECONDS = 900;
    public static final long REFRESH_TOKEN_LIFETIME_SECONDS = 7 * 24 * 3600;

    private final String demoUsername;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final SecretKey jwtSigningKey;
    private final RefreshTokenRepository refreshTokenRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    // Khởi tạo dịch vụ xác thực và kiểm tra secret dùng để ký JWT.
    public AuthService(
            @Value("${app.auth.demo-username:demo}") String demoUsername,
            @Value("${app.auth.jwt-secret-base64:}") String jwtSecretBase64,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            RefreshTokenRepository refreshTokenRepository) {
        if (jwtSecretBase64 == null || jwtSecretBase64.isBlank()) {
            throw new IllegalStateException(
                    "Set JWT_SECRET_BASE64 to a randomly generated Base64 secret of at least 32 bytes");
        }
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretBase64);
        if (keyBytes.length < 32) {
            throw new IllegalStateException("JWT_SECRET_BASE64 must decode to at least 32 bytes for HS256");
        }

        this.demoUsername = demoUsername;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtSigningKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /** Ủy quyền Spring Security xác thực thông tin; nếu đúng thì nâng cấp hash cũ lên BCrypt khi cần. */
    public void authenticate(LoginRequestDTO request) {
        if (request == null || request.getUsername() == null || request.getUsername().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()) {
            throw new BadCredentialsException("Sai username hoặc password");
        }

        // AuthenticationException được ném lên GlobalException để trả HTTP 401 thống nhất.
        authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        request.getUsername(), request.getPassword()));

        userRepository.findByEmail(request.getUsername()).ifPresent(user -> {
            if (user.getPassword() != null && passwordEncoder.upgradeEncoding(user.getPassword())) {
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                userRepository.save(user);
            }
        });
    }

    /** Xác thực thông tin đăng nhập rồi tạo session cho luồng đăng nhập bằng cookie. */
    public void loginWithSession(LoginRequestDTO request, HttpServletRequest servletRequest) {
        authenticate(request);
        HttpSession session = servletRequest.getSession(true);
        servletRequest.changeSessionId();
        session.setAttribute(SESSION_USERNAME, request.getUsername());
    }

    /** Lấy username trong session; ném lỗi xác thực nếu request chưa có session đăng nhập. */
    public String getSessionUsername(HttpServletRequest servletRequest) {
        HttpSession session = servletRequest.getSession(false);
        Object username = session == null ? null : session.getAttribute(SESSION_USERNAME);
        if (!(username instanceof String value) || value.isBlank()) {
            throw new AuthenticationCredentialsNotFoundException("Chưa đăng nhập bằng session");
        }
        return value;
    }

    /** Hủy session đăng nhập hiện tại nếu tồn tại. */
    public void logoutSession(HttpServletRequest servletRequest) {
        HttpSession session = servletRequest.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    /** Xác thực tài khoản rồi tạo access token và refresh token cho luồng đăng nhập stateless. */
    public TokenResponse loginWithToken(LoginRequestDTO request) {
        authenticate(request);
        String accessToken = createAccessToken(request.getUsername());
        String refreshToken = createRefreshToken(request.getUsername());
        return tokenResponse(accessToken, refreshToken);
    }

    /** Xoay vòng refresh token; ném lỗi xác thực nếu token thiếu, hết hạn hoặc không hợp lệ. */
    public TokenResponse refreshTokens(String oldRefreshToken) {
        String username = getRefreshTokenUsername(oldRefreshToken);
        String newRefreshToken = rotateRefreshToken(oldRefreshToken);
        if (newRefreshToken == null || username == null) {
            throw new BadCredentialsException("Refresh token không hợp lệ, hết hạn hoặc đã được sử dụng");
        }
        return tokenResponse(createAccessToken(username), newRefreshToken);
    }

    /** Thu hồi refresh token khi client đăng xuất. */
    public void logoutToken(String refreshToken) {
        revokeRefreshToken(refreshToken);
    }

    /** Kiểm tra header Bearer và trả username từ access token hợp lệ. */
    public String getUsernameFromBearer(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new AuthenticationCredentialsNotFoundException("Thiếu Bearer token");
        }
        String token = authorization.substring("Bearer ".length()).trim();
        if (!isValidToken(token)) {
            throw new BadCredentialsException("Token không hợp lệ hoặc đã hết hạn");
        }
        return getUsernameFromValidToken(token);
    }

    private TokenResponse tokenResponse(String accessToken, String refreshToken) {
        return new TokenResponse(accessToken, "Bearer", TOKEN_LIFETIME_SECONDS,
                refreshToken, REFRESH_TOKEN_LIFETIME_SECONDS);
    }

    /** Tạo access JWT có thời hạn 15 phút, chứa username và role hiện tại để API xác định quyền. */
    public String createAccessToken(String username) {
        Instant now = Instant.now();
        UserRole role = userRepository.findByEmail(username)
                .map(User::getRole)
                .orElse(UserRole.USER);
        return Jwts.builder()
                .subject(username)
                .claim("role", role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(TOKEN_LIFETIME_SECONDS)))
                .signWith(jwtSigningKey)
                .compact();
    }

    /** Tạo refresh token ngẫu nhiên, chỉ lưu hash vào database và trả token gốc đúng một lần cho client. */
    @Transactional
    public String createRefreshToken(String username) {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        String rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        refreshTokenRepository.save(new RefreshToken(hashToken(rawToken), username,
                Instant.now().plusSeconds(REFRESH_TOKEN_LIFETIME_SECONDS)));
        return rawToken;
    }

    /** Thu hồi refresh token hiện tại và xoay vòng bằng token mới nếu token còn hạn và chưa bị thu hồi. */
    @Transactional
    public String rotateRefreshToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return null;
        RefreshToken stored = refreshTokenRepository.findActiveForUpdate(hashToken(rawToken))
                .orElse(null);
        if (stored == null || !stored.getExpiresAt().isAfter(Instant.now())) return null;
        String username = stored.getUsername();
        if (!isDemoUsername(username) && !userRepository.existsByEmail(username)) return null;
        stored.revoke();
        refreshTokenRepository.save(stored);
        return createRefreshToken(username);
    }

    /** Lấy username từ refresh token còn hiệu lực để phát access token mới. */
    @Transactional(readOnly = true)
    public String getRefreshTokenUsername(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return null;
        return refreshTokenRepository.findByTokenHashAndRevokedFalse(hashToken(rawToken))
                .filter(token -> token.getExpiresAt().isAfter(Instant.now()))
                .map(RefreshToken::getUsername)
                .orElse(null);
    }

    /** Thu hồi refresh token khi người dùng đăng xuất; access token hiện tại tự hết hạn theo thời gian. */
    @Transactional
    public void revokeRefreshToken(String rawToken) {
        if (rawToken == null || rawToken.isBlank()) return;
        refreshTokenRepository.findByTokenHashAndRevokedFalse(hashToken(rawToken))
                .ifPresent(token -> { token.revoke(); refreshTokenRepository.save(token); });
    }

    /** Băm refresh token bằng SHA-256 trước khi lưu hoặc tra cứu trong database. */
    private String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
            return java.util.HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable", exception);
        }
    }

    /** Đọc subject (username) từ JWT sau khi xác minh chữ ký và hạn dùng. */
    public String getUsernameFromValidToken(String token) {
        return getClaimsFromValidToken(token).getSubject();
    }

    /** Parse JWT, xác minh chữ ký và trả về claims để filter gắn user cùng role vào request. */
    public Claims getClaimsFromValidToken(String token) {
        return Jwts.parser()
                .verifyWith(jwtSigningKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /** Nhận diện tài khoản demo vì tài khoản này không có bản ghi trong bảng users. */
    public boolean isDemoUsername(String username) {
        return demoUsername.equals(username);
    }

    /** Trả true nếu JWT hợp lệ; token sai định dạng hoặc hết hạn trả false. */
    public boolean isValidToken(String token) {
        try {
            return getUsernameFromValidToken(token) != null;
        } catch (JwtException | IllegalArgumentException exception) {
            return false;
        }
    }
}
