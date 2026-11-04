package com.courseshop.courseshop.dto;

import com.courseshop.courseshop.model.UserRole;

public class UpdateUserRoleRequest {
    private UserRole role;

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }
}
