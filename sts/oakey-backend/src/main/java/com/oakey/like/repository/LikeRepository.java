package com.oakey.like.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oakey.like.domain.Like;
import com.oakey.user.domain.User;
import com.oakey.whisky.domain.Whisky;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserAndWhisky(User user, Whisky whisky);
    boolean existsByUserAndWhisky(User user, Whisky whisky);
    List<Like> findAllByUser(User user);
}