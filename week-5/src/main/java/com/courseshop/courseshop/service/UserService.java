package com.courseshop.courseshop.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.courseshop.courseshop.dto.ResultPaginationDTO;
import com.courseshop.courseshop.dto.event.UserRegisterEvent;
import com.courseshop.courseshop.model.User;
import com.courseshop.courseshop.model.UserRole;
import com.courseshop.courseshop.publisher.RabbitMQJsonProducer;
import com.courseshop.courseshop.repository.UserRepository;
import com.courseshop.courseshop.service.error.EmailAlreadyExistsException;
import com.courseshop.courseshop.service.error.IdInvalidException;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RabbitMQJsonProducer rabbitMQJsonProducer;

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            RabbitMQJsonProducer rabbitMQJsonProducer) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.rabbitMQJsonProducer = rabbitMQJsonProducer;
    }

    // createUser: lớp trung gian giữ tên API service cũ và chuyển sang luồng tạo user chuẩn.
    public User createUser(User user) {
        return handleCreateUser(user);
    }

    // Đăng ký user: kiểm tra/hash dữ liệu, gán USER, lưu DB rồi phát UserRegisterEvent.
    public User handleCreateUser (User user){
        if(userRepository.existsByEmail(user.getEmail())){
            throw new EmailAlreadyExistsException("Email already exists");
        }
        if (user.getPassword() == null || user.getPassword().isBlank()) {
            throw new IllegalArgumentException("Password không được để trống");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(UserRole.USER);
        User savedUser = userRepository.save(user);
        rabbitMQJsonProducer.sendJsonMessage(UserRegisterEvent.from(savedUser));
        return savedUser;
    }

    // Trả danh sách đầy đủ cho các API admin; controller đã giới hạn quyền ADMIN.
    public List<User> getAllUsersForAdmin() {
        return userRepository.findAll();
    }

    // Đổi role theo ID và giữ lại ít nhất một admin để không khóa quyền quản trị hệ thống.
    @CacheEvict(cacheNames = "userProfile", key = "#id")
    public User updateUserRole(Long id, UserRole newRole) {
        if (newRole == null) {
            throw new IllegalArgumentException("role không được để trống");
        }
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy User với id = " + id));

        if (user.getRole() == UserRole.ADMIN && newRole != UserRole.ADMIN
                && userRepository.countByRole(UserRole.ADMIN) <= 1) {
            throw new IllegalArgumentException("Không thể hạ quyền admin cuối cùng");
        }

        user.setRole(newRole);
        return userRepository.save(user);
    }

    // Xóa tài khoản theo ID, trừ trường hợp xóa admin cuối cùng.
    @CacheEvict(cacheNames = "userProfile", key = "#id")
    public void deleteUserForAdmin(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy User với id = " + id));
        if (user.getRole() == UserRole.ADMIN
                && userRepository.countByRole(UserRole.ADMIN) <= 1) {
            throw new IllegalArgumentException("Không thể xóa admin cuối cùng");
        }
        userRepository.delete(user);
    }

    // Lấy tất cả user không phân trang (dùng cho endpoint /users-all).
    public List<User> handleGetAllUser(){
        return this.userRepository.findAll();
    }

    // Tìm một user theo ID; nếu không tồn tại thì ném exception để ExceptionHandle trả 404.
    public User getUserById(Long id) {
        // Cách cũ: trả null để controller tự xử lý trường hợp không tìm thấy.
        // Optional<User> userOptional = this.userRepository.findById(id);
        // return userOptional.orElse(null);
        return this.userRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy User với id = " + id));
    }

    // Cập nhật thông tin user; chỉ hash lại password nếu request có gửi password mới.
    @CacheEvict(cacheNames = "userProfile", key = "#id")
    public User handleUpdateUser(Long id, User inputUser) {
        User currentUser = this.userRepository.findById(id)
                .orElseThrow(() -> new IdInvalidException("Không tìm thấy User để cập nhật với id = " + id));
        currentUser.setName(inputUser.getName());
        currentUser.setEmail(inputUser.getEmail());
        currentUser.setDob(inputUser.getDob());
        currentUser.setCompleted(inputUser.isCompleted());
        if (inputUser.getPassword() != null && !inputUser.getPassword().isBlank()) {
            currentUser.setPassword(passwordEncoder.encode(inputUser.getPassword()));
        }
        return this.userRepository.save(currentUser);
    }

    // Xóa user thường theo ID; nếu ID không tồn tại thì ném exception để trả 404.
    @CacheEvict(cacheNames = "userProfile", key = "#id")
    public boolean handleDeleteUser(Long id) {
        // Cách cũ: trả false khi không tìm thấy User.
        // if (this.userRepository.existsById(id)) {
        //     this.userRepository.deleteById(id);
        //     return true;
        // }
        // return false;
        if (!this.userRepository.existsById(id)) {
            throw new IdInvalidException("Không tìm thấy User để xóa với id = " + id);
        }
        this.userRepository.deleteById(id);
        return true;
    }

    // Lấy một trang user và đóng gói dữ liệu cùng thông tin trang vào DTO.
    public ResultPaginationDTO fetchAllUsersWithPagination(int page, int size) {
        if (page < 1 || size < 1) {
            throw new IllegalArgumentException("page và size phải lớn hơn hoặc bằng 1");
        }
        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> pageUser = this.userRepository.findAll(pageable);

        ResultPaginationDTO dto = new ResultPaginationDTO();
        dto.setData(pageUser.getContent());
        dto.setPage(page);
        dto.setSize(size);
        dto.setTotalPages(pageUser.getTotalPages());
        dto.setTotalElements(pageUser.getTotalElements());

        return dto;
    }
}
