package com.example.coursemanagement_finalprojectmodule03.cache;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class CourseCacheInvalidator {
    private final CourseDetailCache cache;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCourseChanged(CourseChangedEvent event) {
        cache.evict(event.courseId());
    }
}
