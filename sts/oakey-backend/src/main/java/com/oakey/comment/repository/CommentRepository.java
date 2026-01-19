package com.oakey.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oakey.comment.domain.Comment;
import com.oakey.whisky.domain.Whisky;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    // 특정 위스키의 댓글 목록을 최신순으로 조회
    List<Comment> findAllByWhiskyOrderByUpdateDateDesc(Whisky whisky);
}