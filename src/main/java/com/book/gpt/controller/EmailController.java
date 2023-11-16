package com.book.gpt.controller;

import com.book.gpt.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin("*")
@RestController
@RequestMapping("/api")
public class EmailController {
    private final EmailService emailService;

    @Autowired
    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/send-email")
    public ResponseEntity<Boolean> sendEmail(@RequestBody Map<String, String> request) {
        String to = request.get("email");

        // 인증 코드 생성
        String ePw = EmailService.createVerificationCode();

        // 이메일 전송
        boolean isVerificationEmailSent = emailService.sendVerificationEmail(to, ePw);
        // 이메일 인증 코드를 저장
        emailService.saveVerificationCode(to, ePw);

        return new ResponseEntity<>(isVerificationEmailSent, HttpStatus.OK);
    }


    @PostMapping("/verify-email")
    public ResponseEntity<Boolean> verifyEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String verificationCode = request.get("code");

        // 백엔드에서 이메일 및 인증 코드를 확인
        boolean isEmailVerified = emailService.verifyEmail(email, verificationCode);

        return new ResponseEntity<>(isEmailVerified, HttpStatus.OK);
    }

}

