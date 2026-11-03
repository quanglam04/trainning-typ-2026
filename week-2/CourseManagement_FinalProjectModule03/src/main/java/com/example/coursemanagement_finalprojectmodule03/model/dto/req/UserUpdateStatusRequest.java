package com.example.coursemanagement_finalprojectmodule03.model.dto.req;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateStatusRequest {
    @NotNull(message = "Vui lòng để true/false")
    private Boolean isActive;
}
