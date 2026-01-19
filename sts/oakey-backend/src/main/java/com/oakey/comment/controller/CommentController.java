package com.oakey.comment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oakey.comment.dto.CommentRequest;
import com.oakey.comment.dto.CommentResponse;
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

    @GetMapping("/whisky/{wsId}/user/{userId}")
    public ResponseEntity<List<CommentResponse>> getList(
            @PathVariable("wsId") Integer wsId, 
            @PathVariable("userId") Long userId) {
        return ResponseEntity.ok(commentService.getCommentsByWhisky(userId, wsId));
    }
}