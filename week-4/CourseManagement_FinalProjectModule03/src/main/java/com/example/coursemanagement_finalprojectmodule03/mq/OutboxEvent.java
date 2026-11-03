package com.example.coursemanagement_finalprojectmodule03.mq;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "mq_outbox_events")
public class OutboxEvent {
    @Id
    @Column(name = "event_id", length = 36)
    private String id;

    @Column(name = "student_id", nullable = false)
    private Integer studentId;

    @Column(name = "course_id", nullable = false)
    private Integer courseId;

    @Column(name = "course_title", nullable = false)
    private String courseTitle;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    public OutboxEvent(Integer studentId, Integer courseId, String courseTitle) {
        this.id = UUID.randomUUID().toString();
        this.studentId = studentId;
        this.courseId = courseId;
        this.courseTitle = courseTitle;
    }

    public EnrollmentCreatedEvent toMessage() {
        return new EnrollmentCreatedEvent(id, studentId, courseId, courseTitle);
    }
}
