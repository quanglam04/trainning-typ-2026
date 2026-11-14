/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Demo.Spring.Exceptions;

import Demo.Spring.DTO.Response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
/**
 *
 * @author Tran Anh Tuan
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ErrorResponse> handlingNotFound(AppException exception){
        ErrorCode errorcode = exception.getError();
        ErrorResponse errorresponse = new ErrorResponse(errorcode.getCode(), errorcode.getMessage(), errorcode.getDetails());
        return ResponseEntity.badRequest().body(errorresponse);
    }
}
