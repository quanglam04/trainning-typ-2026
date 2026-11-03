package com.example.coursemanagement_finalprojectmodule03.model.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class NotificationCreateRequest {

    @NotNull(message = "ID người nhận (userId) không được để trống")
    private Integer userId;

    @NotBlank(message = "Nội dung thông báo không được để trống")
    private String message;

    private String type;

    private String targetUrl; // Link để Frontend điều hướng khi user click vào
}