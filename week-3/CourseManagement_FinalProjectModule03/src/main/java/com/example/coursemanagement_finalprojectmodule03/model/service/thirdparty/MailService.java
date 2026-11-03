package com.example.coursemanagement_finalprojectmodule03.model.service.thirdparty;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    public void sendMail(String toMail, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toMail);
        message.setSubject("Mã xác thực tài khoản (OTP)");
        message.setText("Mã OTP của bạn là: " + otp + "\nCó hiệu lực trong vòng 5 phút.");
        mailSender.send(message);
    }
}
