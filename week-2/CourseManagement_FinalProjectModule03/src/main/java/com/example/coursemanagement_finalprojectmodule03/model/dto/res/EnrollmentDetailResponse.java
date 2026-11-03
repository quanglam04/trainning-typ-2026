package com.example.coursemanagement_finalprojectmodule03.model.dto.res;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDetailResponse extends EnrollmentResponse{
    List<LessonProgressResponse> lessonProgressResponses;
}
