package com.example.coursemanagement_finalprojectmodule03.mq;

public record EnrollmentCreatedEvent(
        String eventId,
        Integer studentId,
        Integer courseId,
        String courseTitle
) {
}
