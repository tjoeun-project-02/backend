package com.oakey.comment.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oakey.comment.domain.Comment;
import com.oakey.user.domain.User;
import com.oakey.whisky.domain.Whisky;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
	Optional<Comment> findByUserAndWhisky(User user, Whisky whisky);
}