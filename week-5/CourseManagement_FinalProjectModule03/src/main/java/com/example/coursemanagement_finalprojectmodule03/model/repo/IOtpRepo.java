package com.example.coursemanagement_finalprojectmodule03.model.repo;
import com.example.coursemanagement_finalprojectmodule03.model.entity.Otp;
import com.example.coursemanagement_finalprojectmodule03.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IOtpRepo extends JpaRepository<Otp, Integer> {
    Optional<Otp> findByUser(User user);
}
