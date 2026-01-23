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
    
    @Transactional
    public void updateComment(Integer commentId, Long loginUserId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다. ID: " + commentId));

        // 작성자 검증: 댓글의 주인 ID와 현재 토큰의 ID 비교
        if (!comment.getUser().getUserId().equals(loginUserId)) {
            throw new RuntimeException("본인이 작성한 댓글만 수정할 수 있습니다.");
        }

        comment.update(content); // Comment 엔티티의 update 메서드 호출
    }
    
    // 위스키별 댓글 조회
    @Transactional
    public List<CommentResponse> getCommentsByWhisky(Long userId, Integer wsId) {
        // 토큰에서 나온 userId가 실제 DB에 있는지 확인
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("인증된 사용자를 찾을 수 없습니다."));
        
        Whisky whisky = whiskyRepository.findById(wsId)
                .orElseThrow(() -> new IllegalArgumentException("해당 위스키가 존재하지 않습니다."));

        return commentRepository.findByUserAndWhiskyWithJoins(user, whisky).stream()
                .map(c -> new CommentResponse(
                        c.getCommentId(),
                        c.getUser().getNickname(),
                        c.getCommentBody(),
                        c.getUpdateDate().toString(),
                        c.getWhisky().getWsName(),
                        c.getWhisky().getWsNameKo()
                ))
                .collect(Collectors.toList());
    }

	@Transactional
    public void deleteComment(Integer commentId, Long loginUserId) {
        // 1. 댓글 조회 (없으면 에러)
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 댓글입니다. ID: " + commentId));

        // 2. 작성자 검증: 요청한 사람(loginUserId)이 댓글 쓴 사람인지 확인
        if (!comment.getUser().getUserId().equals(loginUserId)) {
            throw new RuntimeException("본인이 작성한 댓글만 삭제할 수 있습니다.");
        }

        // 3. 삭제 수행
        commentRepository.delete(comment);
    }
}