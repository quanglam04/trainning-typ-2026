package com.example.coursemanagement_finalprojectmodule03.model.dto.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL) // Ẩn các trường null
public class ApiResponse<T> {

    private boolean success;

    @JsonProperty("status_code")
    private int statusCode;

    @JsonProperty("error_code")
    private String errorCode;

    private String message;

    private T data; // Dùng khi thành công

    private List<Map<String, String>> errors;

    @Builder.Default
    private String timestamp = LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME);
}