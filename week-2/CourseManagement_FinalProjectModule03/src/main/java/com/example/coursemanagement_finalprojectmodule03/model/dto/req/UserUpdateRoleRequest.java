package com.example.coursemanagement_finalprojectmodule03.model.dto.req;

import com.example.coursemanagement_finalprojectmodule03.constant.Role;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserUpdateRoleRequest {
    @NotNull(message = "Vai trò không được để trống")
    private Role role;
}