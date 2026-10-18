package com.example.coursemanagement_finalprojectmodule03.model.repo;

import com.example.coursemanagement_finalprojectmodule03.constant.Role;
import com.example.coursemanagement_finalprojectmodule03.model.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface IUserRepo extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    boolean existsByEmail(@NotBlank(message = "Email không được để trống") @Email(message = "Email không đúng định dạng") String email);

    boolean existsByEmailIgnoreCase(String email);

    boolean existsByUsername(@NotBlank(message = "Tên đăng nhập không được để trống") @Size(min = 5, max = 50, message = "Tên đăng nhập phải từ 5 đến 50 ký tự") String username);

    Optional<User> findByEmail(@NotBlank(message = "Email không được để trống") @Email(message = "Email không đúng định dạng") String email);

    Optional<User> findByUsername(@NotBlank(message = "Tên đăng nhập không được để trống") String username);

    Page<User> findByRoleAndIsActive(Role role, Boolean isActive, Pageable pageable);

    Page<User> findByRole(Role role, Pageable pageable);

    Page<User> findByIsActive(Boolean isActive, Pageable pageable);
}
