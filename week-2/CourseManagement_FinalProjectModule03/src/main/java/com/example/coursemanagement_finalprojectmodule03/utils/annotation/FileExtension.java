package com.example.coursemanagement_finalprojectmodule03.utils.annotation;

import com.example.coursemanagement_finalprojectmodule03.utils.handler.FileExtensionHandler;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FileExtensionHandler.class)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FileExtension {
    String message();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    String [] allowedFileExtensions();
}
