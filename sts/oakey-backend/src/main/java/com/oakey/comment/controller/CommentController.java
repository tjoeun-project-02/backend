package com.oakey.comment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oakey.comment.dto.CommentRequest;
import com.oakey.comment.dto.CommentResponse;
import com.oakey.comment.dto.CommentUpdateRequest;
import com.oakey.comment.service.CommentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<String> create(@RequestBody CommentRequest request) {
        commentService.saveComment(request);
        return ResponseEntity.ok("댓글이 등록되었습니다.");
    }
    
    @GetMapping("/my")
    public ResponseEntity<List<CommentResponse>> getMyAllComments(
            @AuthenticationPrincipal Object principal) {
        Long userId;
        if (principal instanceof String) {
            userId = Long.parseLong((String) principal);
        } else if (principal instanceof Long) {
            userId = (Long) principal;
        } else {
            throw new RuntimeException("인증 정보를 찾을 수 없습니다.");
        }

        // 모든 댓글을 가져오는 서비스 메서드 호출
        return ResponseEntity.ok(commentService.getCommentsByUserId(userId));
    }
    
    @GetMapping("/whisky/{wsId}/my")
    public ResponseEntity<List<CommentResponse>> getList(
            @PathVariable("wsId") Integer wsId, 
            @AuthenticationPrincipal Object principal) { // Long 대신 Object로 변경
        
        Long userId;
        if (principal instanceof String) {
            userId = Long.parseLong((String) principal);
        } else if (principal instanceof Long) {
            userId = (Long) principal;
        } else {
            // 보안 컨텍스트에 담긴 객체 타입에 따라 적절히 형변환 필요
            throw new RuntimeException("인증 정보를 찾을 수 없습니다.");
        }
        
        return ResponseEntity.ok(commentService.getCommentsByWhisky(userId, wsId));
    }
    
    @PutMapping("/{commentId}")
    public ResponseEntity<String> update(
            @PathVariable("commentId") Integer commentId,
            @AuthenticationPrincipal Object principal, // 토큰에서 유저 ID를 자동으로 꺼냄
            @RequestBody CommentUpdateRequest request) {
        Long userId;
        if (principal instanceof String) {
            userId = Long.parseLong((String) principal);
        } else if (principal instanceof Long) {
            userId = (Long) principal;
        } else {
            throw new RuntimeException("인증 정보를 찾을 수 없습니다.");
        }
        // 서비스 호출 시 토큰에서 꺼낸 ID를 전달
        commentService.updateComment(commentId, userId, request.getContent());
        
        return ResponseEntity.ok("댓글이 수정되었습니다.");
    }
    
    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> delete(
            @PathVariable("commentId") Integer commentId,
            @AuthenticationPrincipal Object principal) { // Object로 변경

        Long userId;
        if (principal instanceof String) {
            userId = Long.parseLong((String) principal);
        } else if (principal instanceof Long) {
            userId = (Long) principal;
        } else {
            throw new RuntimeException("인증 정보를 찾을 수 없습니다.");
        }

        commentService.deleteComment(commentId, userId);
        return ResponseEntity.ok("댓글이 삭제되었습니다.");
    }
}