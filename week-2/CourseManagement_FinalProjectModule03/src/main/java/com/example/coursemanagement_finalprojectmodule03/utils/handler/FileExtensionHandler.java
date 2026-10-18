package com.example.coursemanagement_finalprojectmodule03.utils.handler;

import com.example.coursemanagement_finalprojectmodule03.utils.annotation.FileExtension;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.web.multipart.MultipartFile;

public class FileExtensionHandler implements ConstraintValidator<FileExtension, MultipartFile> {
    private String[] allowedExtensions;

    @Override
    public void initialize(FileExtension constraintAnnotation) {
        allowedExtensions = constraintAnnotation.allowedFileExtensions();
    }

    @Override
    public boolean isValid(MultipartFile value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        String originalName = value.getOriginalFilename();
        if (originalName == null || originalName.isEmpty()) return false;

        String fileNameLowerCase = originalName.toLowerCase();
        for (String extension : allowedExtensions) {
            if (fileNameLowerCase.endsWith(extension.toLowerCase())) {
                return true;
            }
        }
        return false;
    }
}
