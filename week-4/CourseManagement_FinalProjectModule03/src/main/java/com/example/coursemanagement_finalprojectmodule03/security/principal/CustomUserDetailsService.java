package com.example.coursemanagement_finalprojectmodule03.security.principal;

import com.example.coursemanagement_finalprojectmodule03.model.entity.User;
import com.example.coursemanagement_finalprojectmodule03.model.repo.IUserRepo;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final IUserRepo userRepo;

    @Override
    public CustomUserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng với username: " + username));
        return new CustomUserDetails(user);
    }
}
