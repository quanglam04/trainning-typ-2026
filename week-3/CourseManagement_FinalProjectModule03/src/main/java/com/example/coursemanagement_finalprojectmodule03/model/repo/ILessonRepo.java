package com.example.coursemanagement_finalprojectmodule03.model.repo;

import com.example.coursemanagement_finalprojectmodule03.model.entity.Lesson;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ILessonRepo extends JpaRepository<Lesson, Integer> {
    List<Lesson> findByCourseIdAndIsPublishedTrueOrderByOrderIndexAsc(Integer courseId);

    List<Lesson> findByCourseIdOrderByOrderIndexAsc(Integer courseId);

    boolean existsByCourseIdAndOrderIndex(Integer courseId, Integer orderIndex);

    long countByCourseIdAndIsPublishedTrue(Integer courseId);
}
