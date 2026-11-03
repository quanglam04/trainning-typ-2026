package com.example.coursemanagement_finalprojectmodule03.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "lesson_progress", uniqueConstraints = {
        @UniqueConstraint(name = "uq_progress_enrollment_lesson", columnNames = {"enrollment_id", "lesson_id"})
})
public class LessonProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "progress_id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enrollment_id", nullable = false)
    private Enrollment enrollment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @Column(name = "is_completed", nullable = false)
    @Builder.Default
    private boolean isCompleted = false;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @UpdateTimestamp
    @Column(name = "last_accessed_at", nullable = false)
    private LocalDateTime lastAccessedAt;
}
