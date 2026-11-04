package com.courseshop.courseshop.controller;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.courseshop.courseshop.dto.CachedUserDTO;
import com.courseshop.courseshop.dto.UpdateCachedUserRequest;
import com.courseshop.courseshop.service.UserCacheService;

/** Các endpoint riêng để demo chiến lược cache, không làm thay đổi API UserController hiện có. */
@RestController
@RequestMapping("/admin/cache-demo/users")
public class UserCacheController {
    private final UserCacheService userCacheService;
    private final CacheManager cacheManager;

    public UserCacheController(UserCacheService userCacheService, CacheManager cacheManager) {
        this.userCacheService = userCacheService;
        this.cacheManager = cacheManager;
    }

    /** Demo cache-aside: kiểm tra hồ sơ có sẵn trong Redis hay không rồi đọc cache hoặc database. */
    @GetMapping("/cache-aside")
    public ResponseEntity<CachedUserDTO> getUserCacheAside(@RequestParam("id") Long id) {
        Cache cache = cacheManager.getCache("userProfile");
        boolean hit = cache != null && cache.get(id) != null;
        CachedUserDTO user = userCacheService.getUserProfileCacheAside(id);

        return ResponseEntity.ok()
                .header("X-Cache", hit ? "HIT" : "MISS")
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(user);
    }

    /** Demo write-through: cập nhật MySQL và làm mới hồ sơ Redis thông qua @CachePut. */
    @PutMapping("/write-through")
    public ResponseEntity<CachedUserDTO> updateUserWriteThrough(
            @RequestParam("id") Long id,
            @RequestBody UpdateCachedUserRequest request) {
        CachedUserDTO updatedUser = userCacheService.updateUserProfileWriteThrough(id, request);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(updatedUser);
    }
}
