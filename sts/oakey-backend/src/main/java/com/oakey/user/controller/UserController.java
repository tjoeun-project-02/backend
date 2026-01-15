package com.oakey.user.controller;

import com.oakey.security.dto.TokenResponse;
import com.oakey.security.jwt.JwtAuthentication;
import com.oakey.security.service.oauth.OAuthProvider;
import com.oakey.user.dto.LoginRequest;
import com.oakey.user.dto.UserProfileResponse;
import com.oakey.user.dto.UserProfileUpdateRequest;
import com.oakey.user.dto.UserSignupRequest;
import com.oakey.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
        // userService에서 검증 후 토큰 생성 및 반환
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
    
    private Long extractUserId(Authentication authentication) {
        if (authentication instanceof JwtAuthentication jwtAuth) {
            return jwtAuth.getUserId();
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Long) return (Long) principal;
        return Long.parseLong(String.valueOf(principal));
    }
}
