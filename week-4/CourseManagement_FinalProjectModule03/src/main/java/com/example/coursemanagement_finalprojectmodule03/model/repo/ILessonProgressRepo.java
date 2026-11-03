package com.example.coursemanagement_finalprojectmodule03.model.repo;

import com.example.coursemanagement_finalprojectmodule03.model.entity.LessonProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ILessonProgressRepo extends JpaRepository<LessonProgress, Integer> {

    Optional<LessonProgress> findByEnrollmentIdAndLessonId(Integer enrollmentId, Integer lessonId);

    long countByEnrollmentIdAndIsCompletedTrue(Integer enrollmentId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(lp.id) FROM LessonProgress lp WHERE lp.enrollment.id = :enrollmentId AND lp.isCompleted = true AND lp.lesson.isPublished = true")
    long countCompletedPublishedLessons(@org.springframework.data.repository.query.Param("enrollmentId") Integer enrollmentId);

    List<LessonProgress> findByEnrollmentIdAndIsCompletedTrue(Integer enrollmentId);
}