package com.example.coursemanagement_finalprojectmodule03.config;

import com.example.coursemanagement_finalprojectmodule03.constant.CourseStatus;
import com.example.coursemanagement_finalprojectmodule03.constant.EnrollmentStatus;
import com.example.coursemanagement_finalprojectmodule03.constant.Role;
import com.example.coursemanagement_finalprojectmodule03.model.entity.*;
import com.example.coursemanagement_finalprojectmodule03.model.repo.*;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;

    // Inject toàn bộ Repo cần thiết
    private final IUserRepo userRepo;
    private final ICourseRepo courseRepo;
    private final ILessonRepo lessonRepo;
    private final IEnrollmentRepo enrollmentRepo;
    private final ILessonProgressRepo lessonProgressRepo;
    private final IReviewRepo reviewRepo;
    private final INotificationRepo notificationRepo;
    private final IOtpRepo otpRepo;
    private final IRevokedTokenRepo revokedTokenRepo;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepo.existsByUsername("admin_2")) {

            System.out.println(">>> ĐANG DỌN DẸP DỮ LIỆU CŨ...");
            reviewRepo.deleteAll();
            lessonProgressRepo.deleteAll();
            lessonRepo.deleteAll();
            enrollmentRepo.deleteAll();
            courseRepo.deleteAll();
            notificationRepo.deleteAll();
            otpRepo.deleteAll();
            revokedTokenRepo.deleteAll();
            userRepo.deleteAll();

            System.out.println(">>> BẮT ĐẦU KHỞI TẠO DỮ LIỆU TEST (MẶC ĐỊNH MẬT KHẨU: 123456)...");

            // 1. KHỞI TẠO USERS
            String defaultPass = passwordEncoder.encode("123456");

            User admin = userRepo.save(User.builder().username("admin").email("admin@gmail.com").fullName("Quản Trị Viên Gốc").passwordHash(defaultPass).role(Role.ADMIN).isActive(true).build());
            User admin2 = userRepo.save(User.builder().username("admin_2").email("admin2@gmail.com").fullName("Admin Hai").passwordHash(defaultPass).role(Role.ADMIN).isActive(true).build());

            User teacher1 = userRepo.save(User.builder().username("teacher_active").email("teacher1@gmail.com").fullName("Thầy Giáo Một").passwordHash(defaultPass).role(Role.TEACHER).isActive(true).build());
            User teacher2 = userRepo.save(User.builder().username("teacher_banned").email("teacher2@gmail.com").fullName("Thầy Bị Khóa").passwordHash(defaultPass).role(Role.TEACHER).isActive(false).build()); // Bị khóa

            User studentPro = userRepo.save(User.builder().username("student_pro").email("student1@gmail.com").fullName("Trò Giỏi (Học 100%)").passwordHash(defaultPass).role(Role.STUDENT).isActive(true).build());
            User studentLazy = userRepo.save(User.builder().username("student_lazy").email("student2@gmail.com").fullName("Trò Lười (Học 0%)").passwordHash(defaultPass).role(Role.STUDENT).isActive(true).build());
            User studentNew = userRepo.save(User.builder().username("student_new").email("student3@gmail.com").fullName("Trò Mới (Chưa mua)").passwordHash(defaultPass).role(Role.STUDENT).isActive(true).build());

            // 2. KHỞI TẠO KHÓA HỌC
            Course coursePub = courseRepo.save(Course.builder().title("Khóa học Mở").description("Ai cũng xem được").teacher(teacher1).price(new BigDecimal("500000.00")).durationHours(40).status(CourseStatus.PUBLISHED).build());
            Course courseDraft = courseRepo.save(Course.builder().title("Khóa học Đang Soạn").description("Chỉ Admin và Teacher 1 xem được").teacher(teacher1).price(BigDecimal.ZERO).durationHours(10).status(CourseStatus.DRAFT).build());
            Course courseArc = courseRepo.save(Course.builder().title("Khóa học Đã Đóng").description("Cấm đăng ký mới").teacher(teacher2).price(new BigDecimal("300000.00")).durationHours(20).status(CourseStatus.ARCHIVED).build());

            // 3. KHỞI TẠO BÀI HỌC
            Lesson lesson1 = lessonRepo.save(Lesson.builder().course(coursePub).title("Bài 1 (Hiện)").contentUrl("https://vid.com/1").textContent("Nội dung bài 1").orderIndex(1).isPublished(true).build());
            Lesson lesson2 = lessonRepo.save(Lesson.builder().course(coursePub).title("Bài 2 (Hiện)").contentUrl("https://vid.com/2").textContent("Nội dung bài 2").orderIndex(2).isPublished(true).build());
            Lesson lesson3 = lessonRepo.save(Lesson.builder().course(coursePub).title("Bài 3 (Ẩn)").contentUrl("https://vid.com/3").textContent("Nội dung bài 3 đang giấu").orderIndex(3).isPublished(false).build());
            lessonRepo.save(Lesson.builder().course(courseDraft).title("Bài 1 của Khóa Nháp").textContent("Dành cho khóa Draft").orderIndex(1).isPublished(true).build());

            // 4. KHỞI TẠO ĐĂNG KÝ VÀ TIẾN ĐỘ
            Enrollment enrPro = enrollmentRepo.save(Enrollment.builder().student(studentPro).course(coursePub).enrollmentDate(LocalDateTime.now()).status(EnrollmentStatus.COMPLETED).completionDate(LocalDateTime.now()).progressPercentage(new BigDecimal("100.00")).build());
            lessonProgressRepo.save(LessonProgress.builder().enrollment(enrPro).lesson(lesson1).isCompleted(true).completedAt(LocalDateTime.now()).lastAccessedAt(LocalDateTime.now()).build());
            lessonProgressRepo.save(LessonProgress.builder().enrollment(enrPro).lesson(lesson2).isCompleted(true).completedAt(LocalDateTime.now()).lastAccessedAt(LocalDateTime.now()).build());

            enrollmentRepo.save(Enrollment.builder().student(studentLazy).course(coursePub).enrollmentDate(LocalDateTime.now()).status(EnrollmentStatus.ENROLLED).progressPercentage(BigDecimal.ZERO).build());

            // 5. KHỞI TẠO REVIEWS & NOTIFICATIONS
            reviewRepo.save(Review.builder().course(coursePub).student(studentPro).rating(5).comment("Khóa học tuyệt vời, 5 sao!").build());

            notificationRepo.save(Notification.builder().user(studentPro).message("Chào mừng bạn đến với khóa học!").type("SYSTEM").isRead(false).build());
            notificationRepo.save(Notification.builder().user(studentPro).message("Bài học mới đã được thêm.").type("COURSE_UPDATE").targetUrl("/courses/1").isRead(true).build());

            System.out.println("==================================================");
            System.out.println(">>> SEED DỮ LIỆU THÀNH CÔNG! HỆ THỐNG ĐÃ SẴN SÀNG.");
            System.out.println(">>> TÀI KHOẢN: admin_2 | teacher_active | student_pro");
            System.out.println(">>> MẬT KHẨU CHUNG: 123456");
            System.out.println("==================================================");
        }
    }
}
