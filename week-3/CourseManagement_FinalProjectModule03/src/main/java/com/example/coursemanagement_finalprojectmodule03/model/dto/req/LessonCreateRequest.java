package com.example.coursemanagement_finalprojectmodule03.model.dto.req;

import com.example.coursemanagement_finalprojectmodule03.utils.annotation.FileExtension;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class LessonCreateRequest {
    @NotBlank(message = "Tiêu đề bài học không được để trống")
    @Size(max = 255, message = "Tiêu đề bài học không được vượt quá 255 ký tự")
    private String title;

    @FileExtension(allowedFileExtensions = {".mp4", ".avi", ".pdf"}, message = "Chỉ chấp nhận file video hoặc PDF")
    private MultipartFile file;

    private String textContent;

    @NotNull(message = "Thứ tự bài học (Order Index) không được để trống")
    @Min(value = 1, message = "Thứ tự bài học phải bắt đầu từ 1")
    private Integer orderIndex;
}
