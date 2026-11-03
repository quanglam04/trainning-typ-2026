package com.example.coursemanagement_finalprojectmodule03.model.repo;

import com.example.coursemanagement_finalprojectmodule03.model.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IRevokedTokenRepo extends JpaRepository<RevokedToken, Integer> {
    boolean existsByToken(String refreshToken);
}
