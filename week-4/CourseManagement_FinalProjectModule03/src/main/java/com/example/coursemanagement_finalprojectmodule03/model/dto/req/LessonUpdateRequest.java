package com.example.coursemanagement_finalprojectmodule03.model.dto.req;

import com.example.coursemanagement_finalprojectmodule03.utils.annotation.FileExtension;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonUpdateRequest {
    private String title;

    @FileExtension(allowedFileExtensions = {".mp4", ".avi", ".pdf"}, message = "Chỉ chấp nhận file video hoặc PDF")
    private MultipartFile file;

    private String textContent;

    @Min(value = 1, message = "Thứ tự bài học phải bắt đầu từ 1")
    private Integer orderIndex;
}