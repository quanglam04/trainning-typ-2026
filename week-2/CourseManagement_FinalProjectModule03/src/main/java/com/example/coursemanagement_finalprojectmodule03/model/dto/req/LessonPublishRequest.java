package com.example.coursemanagement_finalprojectmodule03.model.dto.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LessonPublishRequest {
    @NotNull(message = "Trạng thái hiển thị không được để trống")
    @JsonProperty("isPublished")
    private Boolean isPublished;
}
