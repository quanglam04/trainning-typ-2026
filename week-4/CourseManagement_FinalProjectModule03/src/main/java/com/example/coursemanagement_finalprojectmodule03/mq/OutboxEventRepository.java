package com.example.coursemanagement_finalprojectmodule03.mq;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxEventRepository extends JpaRepository<OutboxEvent, String> {
    List<OutboxEvent> findTop20BySentAtIsNullOrderByCreatedAtAsc();
}
