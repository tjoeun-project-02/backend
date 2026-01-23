package com.oakey.like.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oakey.like.dto.LikeRequest;
import com.oakey.like.dto.LikedWhiskyResponse;
import com.oakey.like.service.LikeService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/likes")
@RequiredArgsConstructor
public class LikeController {
    private final LikeService likeService;

    @PostMapping
    public ResponseEntity<String> toggleLike(@AuthenticationPrincipal Object principal, @RequestBody LikeRequest requestDto) {
        Long userId;
        if (principal instanceof String) {
            userId = Long.parseLong((String) principal);
        } else if (principal instanceof Long) {
            userId = (Long) principal;
        } else {
            throw new RuntimeException("인증 정보를 찾을 수 없습니다.");
        }
        return ResponseEntity.ok(likeService.toggleLike(userId, requestDto));
    }
    
    // 특정 유저가 좋아요한 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<List<LikedWhiskyResponse>> getLikedWhiskies(@AuthenticationPrincipal Object principal) {
        Long userId;
        if (principal instanceof String) {
            userId = Long.parseLong((String) principal);
        } else if (principal instanceof Long) {
            userId = (Long) principal;
        } else {
            throw new RuntimeException("인증 정보를 찾을 수 없습니다.");
        }
        return ResponseEntity.ok(likeService.getLikedWhiskies(userId));
    }
}