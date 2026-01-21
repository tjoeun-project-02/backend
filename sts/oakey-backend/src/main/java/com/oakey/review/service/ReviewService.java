package com.oakey.review.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.oakey.review.domain.Review;
import com.oakey.review.dto.ReviewCreateRequest;
import com.oakey.review.dto.ReviewResponse;
import com.oakey.review.repository.ReviewRepository;
import com.oakey.whisky.domain.Whisky;
import com.oakey.whisky.repository.WhiskyRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final WhiskyRepository whiskyRepository;

    /*
     * 리뷰 단건 저장 (크롤링 결과)
     */
    public ReviewResponse create(ReviewCreateRequest request) {
        Whisky whisky = whiskyRepository.findById(request.getWsId())
                .orElseThrow(() -> new IllegalArgumentException("위스키를 찾을 수 없습니다."));

        LocalDate reviewDate = LocalDate.parse(request.getReviewDate(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));

        Review review = new Review(
                whisky,
                request.getRwWriter(),
                request.getRawReview(),
                request.getNose(),
                request.getTaste(),
                request.getFinish(),
                request.getRating(),
                reviewDate
        );

        Review saved = reviewRepository.save(review);
        return toResponse(saved);
    }

    /*
     * 위스키 ID 기준 리뷰 목록 조회
     */
    @Transactional(readOnly = true)
    public List<ReviewResponse> findByWhisky(Integer wsId) {
        Whisky whisky = whiskyRepository.findById(wsId)
                .orElseThrow(() -> new IllegalArgumentException("위스키를 찾을 수 없습니다."));
        return reviewRepository.findByWhisky(whisky)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ReviewResponse toResponse(Review review) {
        return new ReviewResponse(
                review.getRwId(),
                review.getWhisky().getWsId(),
                review.getRwWriter(),
                review.getRawReview(),
                review.getNose(),
                review.getTaste(),
                review.getFinish(),
                review.getRating(),
                review.getReviewDate().toString()
        );
    }
}
