package com.oakey.emailVerification.service;

import java.time.LocalDateTime;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.oakey.emailVerification.domain.EmailVerification;
import com.oakey.emailVerification.repository.EmailVerificationRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository repo;

    public void sendVerificationCode(String email) {
        String code = generateCode();
        LocalDateTime expires = LocalDateTime.now().plusMinutes(3);

        EmailVerification ev = new EmailVerification();
        ev.setEmail(email);
        ev.setCode(code);
        ev.setExpiresAt(expires);
        repo.save(ev);

        sendEmail(email, code);
    }

    private void sendEmail(String email, String code) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Oakey 인증코드");
        msg.setText("인증코드는 다음과 같습니다: " + code + " (3분 이내 입력)");

        mailSender.send(msg);
    }

    private String generateCode() {
        return String.valueOf((int)(Math.random() * 900000 + 100000));
    }

    public boolean verifyCode(String email, String code) {
        return repo.findById(email)
                .filter(ev -> ev.getCode().equals(code))
                .filter(ev -> ev.getExpiresAt().isAfter(LocalDateTime.now()))
                .isPresent();
    }
}