package com.oakey.review.service;

import com.oakey.review.domain.Review;
import com.oakey.review.dto.ReviewCreateRequest;
import com.oakey.review.dto.ReviewResponse;
import com.oakey.review.repository.ReviewRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ReviewService {

    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    /*
     * 리뷰 단건 저장 (크롤링 결과)
     */
    public ReviewResponse create(ReviewCreateRequest request) {

        Review review = new Review(
                request.getWsId(),
                request.getWsName(),
                request.getWsNameKo(),
                request.getRwWriter(),
                request.getRawReview(),
                request.getNose(),
                request.getTaste(),
                request.getFinish(),
                request.getRating(),
                request.getReviewDate()
        );

        Review saved = reviewRepository.save(review);
        return toResponse(saved);
    }

    /*
     * 위스키 ID 기준 리뷰 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> findByWhisky(Integer wsId) {
        return reviewRepository.findByWsId(wsId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getRwId(),
                review.getWsId(),
                review.getWsName(),
                review.getWsNameKo(),
                review.getRwWriter(),
                review.getRawReview(),
                review.getNose(),
                review.getTaste(),
                review.getFinish(),
                review.getRating(),
                review.getReviewDate()
        );
    }
}
