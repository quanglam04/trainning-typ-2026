package com.courseshop.courseshop.dto;

import java.io.Serializable;
import java.time.LocalDate;

/** DTO an toàn cho demo Redis: không chứa mật khẩu đã băm hoặc quan hệ JPA. */
public record CachedUserDTO(
        Long id,
        String name,
        String email,
        LocalDate dob,
        boolean completed) implements Serializable {
}
