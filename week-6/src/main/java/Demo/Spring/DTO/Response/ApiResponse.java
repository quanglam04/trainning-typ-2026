/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package Demo.Spring.DTO.Response;

import com.fasterxml.jackson.annotation.JsonInclude;
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


public class ApiResponse{
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private int code;
    private String message;
}
