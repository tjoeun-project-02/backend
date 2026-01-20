package com.oakey.comment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.oakey.comment.domain.Comment;
import com.oakey.user.domain.User;
import com.oakey.whisky.domain.Whisky;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
	Optional<Comment> findByUserAndWhisky(User user, Whisky whisky);

	// CommentRepository.java
	@Query("SELECT c FROM Comment c " +
	       "JOIN FETCH c.whisky " +
	       "JOIN FETCH c.user " + // User도 페치 조인 추가!
	       "WHERE c.user = :user AND c.whisky = :whisky")
	List<Comment> findByUserAndWhiskyWithJoins(@Param("user") User user, @Param("whisky") Whisky whisky);
}