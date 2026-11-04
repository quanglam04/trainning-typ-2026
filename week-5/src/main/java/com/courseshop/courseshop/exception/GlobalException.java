package com.courseshop.courseshop.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.courseshop.courseshop.dto.RestErrorDTO;
import com.courseshop.courseshop.service.error.EmailAlreadyExistsException;
import com.courseshop.courseshop.service.error.IdInvalidException;

/** Tập trung chuyển exception từ controller/service thành HTTP response thống nhất. */
@RestControllerAdvice
public class GlobalException {

    // Thông tin đăng nhập sai hoặc tài khoản không tồn tại: trả 401 với thông báo chung.
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RestErrorDTO> handleAuthentication(AuthenticationException exception) {
        return error(HttpStatus.UNAUTHORIZED, "Sai username hoặc password");
    }

    // ID không tồn tại: chuyển exception từ service thành HTTP 404.
    @ExceptionHandler(IdInvalidException.class)
    public ResponseEntity<RestErrorDTO> handleIdInvalid(IdInvalidException exception) {
        return error(HttpStatus.NOT_FOUND, exception.getMessage());
    }

    // Email đã được đăng ký: trả HTTP 409 để báo xung đột dữ liệu.
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<RestErrorDTO> handleEmailAlreadyExists(EmailAlreadyExistsException exception) {
        return error(HttpStatus.CONFLICT, exception.getMessage());
    }

    // Tham số hoặc JSON gửi lên không hợp lệ: trả HTTP 400.
    @ExceptionHandler({
            IllegalArgumentException.class,
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class
    })
    public ResponseEntity<RestErrorDTO> handleBadRequest(Exception exception) {
        return error(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    // Lỗi validation của request body: trả tên field cùng thông báo lỗi đầu tiên.
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RestErrorDTO> handleValidation(MethodArgumentNotValidException exception) {
        String message = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse("Dữ liệu không hợp lệ");
        return error(HttpStatus.BAD_REQUEST, message);
    }

    /** Tạo response lỗi thống nhất gồm status, tên lỗi và nội dung thông báo. */
    private ResponseEntity<RestErrorDTO> error(HttpStatus status, String message) {
        return ResponseEntity.status(status)
                .body(new RestErrorDTO(status.value(), status.getReasonPhrase(), message));
    }
}
