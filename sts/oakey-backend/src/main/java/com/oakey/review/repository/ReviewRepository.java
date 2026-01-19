package com.oakey.review.repository;

import com.oakey.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/*
 * 리뷰 조회/저장용 Repository
 */
public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByWsId(Integer wsId);
}
