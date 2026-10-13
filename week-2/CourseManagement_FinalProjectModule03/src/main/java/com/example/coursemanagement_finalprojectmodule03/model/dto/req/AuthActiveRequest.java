package com.example.coursemanagement_finalprojectmodule03.model.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthActiveRequest {

    @NotBlank(message = "Mã OTP không được để trống")
    @Pattern(regexp = "^\\d{6}$", message = "Mã OTP phải là 6 chữ số")
    private String otp;

    @NotBlank(message = "Email không được để trống")
    @Email(message = "Email không đúng định dạng")
    private String email;
}
