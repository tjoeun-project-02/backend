package com.oakey.emailVerification.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oakey.emailVerification.service.EmailVerificationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/email")
public class EmailVerificationController {

    private final EmailVerificationService service;

    @PostMapping("/send-code")
    public ResponseEntity<?> sendCode(@RequestBody Map<String, String> req) {
        service.sendVerificationCode(req.get("email"));
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> req) {
        boolean valid = service.verifyCode(req.get("email"), req.get("code"));
        return ResponseEntity.ok(Map.of("valid", valid));
    }
}