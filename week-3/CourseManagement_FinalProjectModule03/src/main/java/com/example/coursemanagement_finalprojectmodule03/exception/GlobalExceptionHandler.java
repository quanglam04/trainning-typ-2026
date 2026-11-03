package com.example.coursemanagement_finalprojectmodule03.exception;

import com.example.coursemanagement_finalprojectmodule03.model.dto.wrapper.ApiResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.*;
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. Lỗi Validation dữ liệu đầu vào DTO (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errorList = new ArrayList<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            Map<String, String> errorDetail = new HashMap<>();
            errorDetail.put("field", error.getField());
            errorDetail.put("message", error.getDefaultMessage());
            errorList.add(errorDetail);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Void>builder()
                        .success(false)
                        .statusCode(400)
                        .errorCode("INVALID_INPUT_DATA")
                        .message("Dữ liệu đầu vào không hợp lệ")
                        .errors(errorList)
                        .build()
        );
    }

    // 2. Lỗi xung đột tài nguyên (Duplicate/Conflict nghiệp vụ)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiResponse<Void>> handleConflict(ConflictException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(
                ApiResponse.<Void>builder()
                        .success(false)
                        .statusCode(409)
                        .errorCode("DUPLICATE_RESOURCE")
                        .message(ex.getMessage())
                        .build()
        );
    }

    // 3. Lỗi vi phạm ràng buộc dữ liệu CSDL (Unique Constraint, Foreign Key Constraint)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Void>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("DataIntegrityViolationException: ", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Void>builder()
                        .success(false)
                        .statusCode(400)
                        .errorCode("DUPLICATE_RESOURCE")
                        .message("Dữ liệu bị trùng lặp hoặc vi phạm ràng buộc liên kết dữ liệu trong hệ thống!")
                        .build()
        );
    }

    // 4. Lỗi dữ liệu yêu cầu không hợp lệ
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Void>builder()
                        .success(false)
                        .statusCode(400)
                        .errorCode("INVALID_INPUT_DATA")
                        .message(ex.getMessage())
                        .build()
        );
    }

    // 5. Lỗi định dạng JSON hoặc sai Enum trong Request Body
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String defaultMessage = "Dữ liệu gửi lên không đúng định dạng JSON hoặc sai kiểu dữ liệu!";

        Throwable cause = ex.getMostSpecificCause();

        if (cause instanceof com.fasterxml.jackson.databind.exc.InvalidFormatException ife) {
            if (ife.getTargetType() != null && ife.getTargetType().isEnum()) {
                defaultMessage = String.format("Giá trị '%s' không hợp lệ! Các giá trị hợp lệ gồm: %s",
                        ife.getValue(), java.util.Arrays.toString(ife.getTargetType().getEnumConstants()));
            }
        }

        if (defaultMessage.startsWith("Dữ liệu gửi lên") && cause.getMessage() != null) {
            String msg = cause.getMessage();
            if (msg.contains("not one of the values accepted for Enum class")) {
                try {
                    String validValues = msg.substring(msg.indexOf("["), msg.indexOf("]") + 1);
                    defaultMessage = "Giá trị trạng thái không hợp lệ! Chỉ chấp nhận: " + validValues;
                } catch (Exception ignored) {
                    defaultMessage = "Giá trị Enum truyền vào không hợp lệ!";
                }
            }
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Void>builder()
                        .success(false)
                        .statusCode(400)
                        .errorCode("INVALID_INPUT_DATA")
                        .message(defaultMessage)
                        .build()
        );
    }

    // 6. Lỗi sai kiểu dữ liệu / sai Enum trên URL Query Param và PathVariable
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = String.format("Tham số '%s' nhận giá trị không hợp lệ: '%s'", ex.getName(), ex.getValue());

        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            message = String.format("Giá trị '%s' của tham số '%s' không hợp lệ! Các giá trị hợp lệ gồm: %s",
                    ex.getValue(), ex.getName(), Arrays.toString(ex.getRequiredType().getEnumConstants()));
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Void>builder()
                        .success(false)
                        .statusCode(400)
                        .errorCode("INVALID_INPUT_DATA")
                        .message(message)
                        .build()
        );
    }

    // 7. Lỗi không tìm thấy tài nguyên
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponse.<Void>builder()
                        .success(false)
                        .statusCode(404)
                        .errorCode("RESOURCE_NOT_FOUND")
                        .message(ex.getMessage())
                        .build()
        );
    }

    // 8. Lỗi xác thực tài khoản / mật khẩu
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ApiResponse<Void>> handleAuthException(AuthException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponse.<Void>builder()
                        .success(false)
                        .statusCode(401)
                        .errorCode("BAD_CREDENTIALS")
                        .message(ex.getMessage())
                        .build()
        );
    }

    // 9. Lỗi phân quyền truy cập
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex) {
        String rawMessage = ex.getMessage();
        String message = "Bạn không có quyền thực hiện chức năng này!";

        if (rawMessage != null
                && !rawMessage.trim().isEmpty()
                && !rawMessage.equalsIgnoreCase("Access Denied")
                && !rawMessage.equalsIgnoreCase("Access is denied")) {
            message = rawMessage;
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResponse.<Void>builder()
                        .success(false)
                        .statusCode(403)
                        .errorCode("ACCESS_DENIED")
                        .message(message)
                        .build()
        );
    }

    // 10. Lỗi vượt quá dung lượng upload file
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponse.<Void>builder()
                        .success(false)
                        .statusCode(400)
                        .errorCode("INVALID_INPUT_DATA")
                        .message("Dung lượng tệp tải lên vượt quá giới hạn cho phép (Tối đa 100MB)!")
                        .build()
        );
    }

    // 11. Lỗi hệ thống không lường trước
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception ex) {
        log.error("Lỗi hệ thống 500: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.<Void>builder()
                        .success(false)
                        .statusCode(500)
                        .errorCode("INTERNAL_SERVER_ERROR")
                        .message("Lỗi ngoại lệ hệ thống không lường trước")
                        .build()
        );
    }
}
