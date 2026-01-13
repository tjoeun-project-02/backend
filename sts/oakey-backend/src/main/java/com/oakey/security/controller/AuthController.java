package com.oakey.security.controller;

import com.oakey.security.dto.RefreshRequest;
import com.oakey.security.dto.TokenResponse;
import com.oakey.security.service.AuthService;
import com.oakey.security.service.oauth.OAuthProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/kakao")
    public ResponseEntity<TokenResponse> kakaoLogin(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return ResponseEntity.ok(authService.loginWithProvider(OAuthProvider.KAKAO, authorization));
    }

    @PostMapping("/google")
    public ResponseEntity<TokenResponse> googleLogin(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorization) {
        return ResponseEntity.ok(authService.loginWithProvider(OAuthProvider.GOOGLE, authorization));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        return ResponseEntity.ok(authService.refresh(req.getRefreshToken()));
    }
}
