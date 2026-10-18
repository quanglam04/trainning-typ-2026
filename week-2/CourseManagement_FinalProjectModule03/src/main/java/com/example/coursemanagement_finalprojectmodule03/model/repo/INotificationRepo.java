package com.example.coursemanagement_finalprojectmodule03.model.repo;

import com.example.coursemanagement_finalprojectmodule03.model.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface INotificationRepo extends JpaRepository<Notification, Integer> {

    Page<Notification> findByUserIdOrderByCreatedAtDesc(Integer userId, Pageable pageable);

    Optional<Notification> findByIdAndUserId(Integer id, Integer userId);
}
