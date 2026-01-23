package com.oakey.user.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oakey.security.dto.TokenResponse;
import com.oakey.security.jwt.JwtAuthentication;
import com.oakey.user.dto.LoginRequest;
import com.oakey.user.dto.PasswordChangeRequest;
import com.oakey.user.dto.ResetPasswordRequest;
import com.oakey.user.dto.UserProfileResponse;
import com.oakey.user.dto.UserProfileUpdateRequest;
import com.oakey.user.dto.UserSignupRequest;
import com.oakey.user.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@Valid @RequestBody UserSignupRequest req) {
        return ResponseEntity.ok(userService.signup(req));
    }

    @PostMapping("/emaillogin")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(userService.login(req));
    }

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> me(Authentication authentication) {
        Long userId = extractUserId(authentication);
        return ResponseEntity.ok(userService.getMyProfile(userId));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserProfileResponse> updateMe(
            Authentication authentication,
            @RequestBody UserProfileUpdateRequest req
    ) {
        Long userId = extractUserId(authentication);
        return ResponseEntity.ok(userService.updateMyProfile(userId, req));
    }

    /**
     * 비밀번호 변경
     */
    @PatchMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            Authentication authentication,
            @RequestBody PasswordChangeRequest req
    ) {
        Long userId = extractUserId(authentication);
        userService.changeMyPassword(userId, req);
        return ResponseEntity.noContent().build();
    }
    
    /*
     * 비밀번호 재설정
     */
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest req) {
        // 1. 서비스에서 이메일과 인증코드가 유효한지 최종 확인
        // 2. 해당 이메일 사용자의 비밀번호를 BCrypt 암호화하여 DB 업데이트
        userService.resetPassword(req);
        return ResponseEntity.ok().build();
    }
    
    /**
     * JWT Authentication에서 userId 추출
     */
    private Long extractUserId(Authentication authentication) {

        if (authentication == null) {
            throw new IllegalArgumentException("인증 정보가 없습니다.");
        }

        if (authentication instanceof JwtAuthentication jwtAuth) {
            return jwtAuth.getUserId();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Long) {
            return (Long) principal;
        }

        try {
            return Long.parseLong(String.valueOf(principal));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("인증 사용자 식별자를 파싱할 수 없습니다.");
        }
    }
}
