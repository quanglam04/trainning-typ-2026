package com.courseshop.courseshop.dto;

/** Các trường được phép sửa trong demo write-through; không cho đổi mật khẩu hoặc quyền tại đây. */
public record UpdateCachedUserRequest(String name, boolean completed) {
}
