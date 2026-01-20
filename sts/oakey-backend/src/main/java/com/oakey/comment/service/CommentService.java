package com.oakey.comment.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.oakey.comment.domain.Comment;
import com.oakey.comment.dto.CommentRequest;
import com.oakey.comment.dto.CommentResponse;
import com.oakey.comment.repository.CommentRepository;
import com.oakey.user.domain.User;
import com.oakey.user.repository.UserRepository;
import com.oakey.whisky.domain.Whisky;
import com.oakey.whisky.repository.WhiskyRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final WhiskyRepository whiskyRepository;

    // 댓글 등록
    public void saveComment(CommentRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을 수 없습니다."));
        Whisky whisky = whiskyRepository.findById(request.getWsId())
                .orElseThrow(() -> new IllegalArgumentException("위스키를 찾을 수 없습니다."));

        Comment comment = Comment.builder()
                .user(user)
                .whisky(whisky)
                .commentBody(request.getContent())
                .build();

        commentRepository.save(comment);
    }

    // 위스키별 댓글 조회
    @Transactional
    public List<CommentResponse> getCommentsByWhisky(Long userId, Integer wsId) {
        User user = userRepository.findById(userId).orElseThrow();
    	Whisky whisky = whiskyRepository.findById(wsId)
    			.orElseThrow(() -> new IllegalArgumentException("위스키를 찾을 수 없습니다."));

        return commentRepository.findByUserAndWhisky(user, whisky).stream()
                .map(c -> new CommentResponse(
                        c.getCommentId(),
                        c.getUser().getNickname(),
                        c.getCommentBody(),
                        c.getUpdateDate().toString()
                ))
                .collect(Collectors.toList());
    }
}