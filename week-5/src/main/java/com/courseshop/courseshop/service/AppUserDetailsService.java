package com.courseshop.courseshop.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.courseshop.courseshop.model.User;
import com.courseshop.courseshop.model.UserRole;
import com.courseshop.courseshop.repository.UserRepository;

/** Tải thông tin xác thực từ database cho AuthenticationManager; hỗ trợ thêm tài khoản demo cấu hình sẵn. */
@Service
public class AppUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final String demoUsername;
    private final String demoPasswordHash;

    /** Tạo service và hash mật khẩu demo một lần để DaoAuthenticationProvider kiểm tra như user thường. */
    public AppUserDetailsService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.auth.demo-username:demo}") String demoUsername,
            @Value("${app.auth.demo-password:demo123}") String demoPassword) {
        this.userRepository = userRepository;
        this.demoUsername = demoUsername;
        this.demoPasswordHash = passwordEncoder.encode(demoPassword);
    }

    /** Tìm user theo email, hoặc trả tài khoản demo nếu email trùng username demo đã cấu hình. */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByEmail(username)
                .map(this::toUserDetails)
                .orElseGet(() -> {
                    if (demoUsername.equals(username)) {
                        return org.springframework.security.core.userdetails.User
                                .withUsername(demoUsername)
                                .password(demoPasswordHash)
                                .roles(UserRole.USER.name())
                                .build();
                    }
                    throw new UsernameNotFoundException("Không tìm thấy tài khoản: " + username);
                });
    }

    private UserDetails toUserDetails(User user) {
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPassword())
                .roles(user.getRole().name())
                .build();
    }
}
