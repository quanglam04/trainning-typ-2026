package com.courseshop.courseshop.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.courseshop.courseshop.dto.event.UserRegisterEvent;

/** Gửi email welcome nếu SMTP đã cấu hình; nếu chưa, ghi log mô phỏng để demo consumer. */
@Service
public class WelcomeEmailService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WelcomeEmailService.class);

    private final ObjectProvider<JavaMailSender> mailSenderProvider;
    private final String fromAddress;
    private final boolean mailEnabled;

    public WelcomeEmailService(
            ObjectProvider<JavaMailSender> mailSenderProvider,
            @Value("${app.mail.from:no-reply@courseshop.local}") String fromAddress,
            @Value("${app.mail.enabled:false}") boolean mailEnabled) {
        this.mailSenderProvider = mailSenderProvider;
        this.fromAddress = fromAddress;
        this.mailEnabled = mailEnabled;
    }

    /** Gửi welcome email đến địa chỉ trong event, hoặc log nội dung demo nếu chưa có SMTP. */
    public void sendWelcomeEmail(UserRegisterEvent event) {
        if (!mailEnabled) {
            LOGGER.info("[DEMO EMAIL] Welcome email to {} for user {} (bật MAIL_ENABLED để gửi SMTP thật)",
                    event.getEmail(), event.getName());
            return;
        }

        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();
        if (mailSender == null) {
            throw new IllegalStateException("MAIL_ENABLED=true nhưng Spring chưa tạo JavaMailSender; kiểm tra cấu hình SMTP");
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromAddress);
        message.setTo(event.getEmail());
        message.setSubject("Chào mừng bạn đến với CourseShop");
        message.setText("Xin chào " + event.getName()
                + ",\n\nChào mừng bạn đến với CourseShop. Cảm ơn bạn đã đăng ký tài khoản!");
        mailSender.send(message);

        LOGGER.info("Welcome email sent to {} (eventId={})", event.getEmail(), event.getEventId());
    }
}
