package com.courseshop.courseshop.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.courseshop.courseshop.dto.ResultPaginationDTO;
import com.courseshop.courseshop.model.User;
import com.courseshop.courseshop.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    // GET /users/{id}: lấy thông tin một user; quyền truy cập được giới hạn trong SecurityConfig.
    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User userData = this.userService.getUserById(id);
        // Cách cũ: controller tự kiểm tra null và tự tạo response 404.
        // if (userData == null) {
        //     return ResponseEntity.status(HttpStatus.NOT_FOUND)
        //             .body(new RestErrorDTO(404, "Not Found", "Không tìm thấy User với id = " + id));
        // }
        return ResponseEntity.ok(userData);
    }

    // GET /users?page=1&size=10: lấy danh sách user theo từng trang.
    @GetMapping
    public ResponseEntity<ResultPaginationDTO> getUsers(
            @RequestParam(name = "page", defaultValue = "1") int page,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        
        ResultPaginationDTO result = this.userService.fetchAllUsersWithPagination(page, size);
        return ResponseEntity.ok().body(result);
    }

    // POST /users: đăng ký user; service lưu tài khoản rồi phát event để consumer gửi email welcome.
    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User input) {
        User newUser = this.userService.handleCreateUser(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
    }

    // PUT /users/{id}: cập nhật thông tin user theo ID.
    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User input) {
        User updatedUser = this.userService.handleUpdateUser(id, input);
        // Cách cũ: service trả null nếu không tìm thấy; controller tự trả 404.
        // if (updatedUser == null) {
        //     return ResponseEntity.status(HttpStatus.NOT_FOUND)
        //             .body(new RestErrorDTO(404, "Not Found", "Không tìm thấy User để cập nhật với id = " + id));
        // }
        return ResponseEntity.ok(updatedUser);
    }
    
    // DELETE /users/{id}: xóa user theo ID.
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        this.userService.handleDeleteUser(id);
        // Cách cũ: service trả false nếu không tìm thấy; controller tự trả 404.
        // boolean isDeleted = this.userService.handleDeleteUser(id);
        // if (!isDeleted) {
        //     return ResponseEntity.status(HttpStatus.NOT_FOUND)
        //             .body(new RestErrorDTO(404, "Not Found", "Không tìm thấy User để xóa với id = " + id));
        // }
        return ResponseEntity.ok("Xóa thành công user có id = " + id);
    }

    // GET /users/all: lấy toàn bộ user không phân trang (chỉ ADMIN được phép).
    @GetMapping("/all")
    public ResponseEntity<List<User>> getAllUser() {
        List<User> listUser = this.userService.handleGetAllUser();
        return ResponseEntity.ok().body(listUser);
    }

}
