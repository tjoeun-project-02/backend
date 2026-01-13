package com.oakey.user.controller;

import com.oakey.user.dto.SocialProfileRequest;
import com.oakey.user.dto.UserSignupRequest;
import com.oakey.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    // 자체 회원가입(구조용 뼈대 + 동작 확인용)
    @PostMapping("/signup")
    public ResponseEntity<Long> signup(@Valid @RequestBody UserSignupRequest req) {
        Long userId = userService.signup(req);
        return ResponseEntity.ok(userId);
    }

    // 소셜 로그인 후 추가정보 입력(동작 확인용 임시 API)
    // 실제 서비스에서는 Security에서 로그인된 userId를 꺼내서 처리하는 방식으로 변경 예정
    @PostMapping("/{userId}/social-profile")
    public ResponseEntity<Void> completeSocialProfile(
            @PathVariable Long userId,
            @Valid @RequestBody SocialProfileRequest req
    ) {
        userService.completeSocialProfile(userId, req);
        return ResponseEntity.ok().build();
    }
}
