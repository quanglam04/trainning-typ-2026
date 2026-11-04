package com.courseshop.courseshop.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;

import com.courseshop.courseshop.dto.CachedUserDTO;
import com.courseshop.courseshop.dto.UpdateCachedUserRequest;
import com.courseshop.courseshop.model.User;
import com.courseshop.courseshop.repository.UserRepository;
import com.courseshop.courseshop.service.error.IdInvalidException;

/** Service minh họa hai chiến lược cache cho hồ sơ người dùng. */
@Service
public class UserCacheService {
    private final UserRepository userRepository;

    public UserCacheService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /** Cache-aside: kiểm tra Redis trước; nếu miss thì đọc MySQL và lưu kết quả vào cache. */
    @Cacheable(cacheNames = "userProfile", key = "#id", unless = "#result == null")
    public CachedUserDTO getUserProfileCacheAside(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy User với id = " + id));

        return new CachedUserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getDob(),
                user.isCompleted());
    }

    /** Write-through: luôn cập nhật MySQL, sau đó ghi DTO trả về vào Redis bằng cùng key. */
    @CachePut(cacheNames = "userProfile", key = "#id", unless = "#result == null")
    public CachedUserDTO updateUserProfileWriteThrough(Long id, UpdateCachedUserRequest request) {
        if (request.name() == null || request.name().isBlank()) {
            throw new IllegalArgumentException("Tên user không được để trống");
        }

        User user = userRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy User với id = " + id));
        user.setName(request.name().trim());
        user.setCompleted(request.completed());

        // Lưu MySQL trước; khi method thành công, @CachePut ghi hồ sơ trả về vào Redis.
        User savedUser = userRepository.save(user);
        return new CachedUserDTO(
                savedUser.getId(),
                savedUser.getName(),
                savedUser.getEmail(),
                savedUser.getDob(),
                savedUser.isCompleted());
    }
}
