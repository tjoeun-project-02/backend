package com.oakey.comment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @GetMapping("/whisky/{wsId}/my")
    public ResponseEntity<List<CommentResponse>> getList(
            @PathVariable("wsId") Integer wsId, 
            @AuthenticationPrincipal Long userId) { // 토큰에서 추출된 userId가 자동으로 주입됨
        
        return ResponseEntity.ok(commentService.getCommentsByWhisky(userId, wsId));
    }
    
    @PutMapping("/{commentId}")
    public ResponseEntity<String> update(
            @PathVariable("commentId") Integer commentId,
            @AuthenticationPrincipal Long loginUserId, // 토큰에서 유저 ID를 자동으로 꺼냄
            @RequestBody CommentUpdateRequest request) {

        // 서비스 호출 시 토큰에서 꺼낸 ID를 전달
        commentService.updateComment(commentId, loginUserId, request.getContent());
        
        return ResponseEntity.ok("댓글이 수정되었습니다.");
    }
}