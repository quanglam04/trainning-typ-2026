package com.example.coursemanagement_finalprojectmodule03.model.repo.spec;

import com.example.coursemanagement_finalprojectmodule03.constant.CourseStatus;
import com.example.coursemanagement_finalprojectmodule03.constant.Role;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Course;
import com.example.coursemanagement_finalprojectmodule03.security.principal.CustomUserDetails;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class CourseSpecification {
    public static Specification<Course> filterCourses(String keyword, Integer teacherId, String status, CustomUserDetails currentUser) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                String likePattern = "%" + keyword.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), likePattern),
                        cb.like(cb.lower(root.get("description")), likePattern)
                ));
            }

            if (teacherId != null) {
                predicates.add(cb.equal(root.get("teacher").get("id"), teacherId));
            }

            if (status != null && !status.trim().isEmpty()) {
                try {
                    CourseStatus courseStatus = CourseStatus.valueOf(status.trim().toUpperCase());
                    predicates.add(cb.equal(root.get("status"), courseStatus));
                } catch (IllegalArgumentException e) {
                    predicates.add(cb.disjunction());
                }
            }

            if (currentUser == null || currentUser.getUser().getRole() == Role.STUDENT) {
                predicates.add(cb.equal(root.get("status"), CourseStatus.PUBLISHED));
            }
            else if (currentUser.getUser().getRole() == Role.TEACHER) {
                Integer currentTeacherId = currentUser.getUser().getId();
                Predicate isPublished = cb.equal(root.get("status"), CourseStatus.PUBLISHED);
                Predicate isOwnerTeacher = cb.equal(root.get("teacher").get("id"), currentTeacherId);

                predicates.add(cb.or(isPublished, isOwnerTeacher));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
