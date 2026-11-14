/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Demo.Spring.DTO.Response;

//import org.springframework.http.HttpStatusCode;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author Tran Anh Tuan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"code", "message", "details"})
public class ErrorResponse extends ApiResponse {
    private String details;
    public ErrorResponse(int code, String message, String details) {
        super(code, message);
        this.details = details;
    }
}
