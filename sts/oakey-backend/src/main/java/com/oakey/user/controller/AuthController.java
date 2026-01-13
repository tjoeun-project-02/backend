package com.oakey.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class AuthController {

    // 소셜 로그인 성공 시: 로그인된 사용자 정보를 JSON으로 확인하는 용도
    @GetMapping("/login/success")
    public ResponseEntity<Map<String, Object>> success(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "No OAuth2User (not authenticated)"));
        }
        return ResponseEntity.ok(oAuth2User.getAttributes());
    }

    // 소셜 로그인 실패 시
    @GetMapping("/login/failure")
    public ResponseEntity<Map<String, Object>> failure() {
        return ResponseEntity.badRequest().body(Map.of("error", "OAuth2 Login Failed"));
    }
}
