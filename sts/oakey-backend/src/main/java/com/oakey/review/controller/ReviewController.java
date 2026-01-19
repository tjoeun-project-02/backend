package com.oakey.review.controller;

import com.oakey.review.dto.ReviewCreateRequest;
import com.oakey.review.dto.ReviewResponse;
import com.oakey.review.service.ReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /*
     * 크롤링 리뷰 저장
     */
    @PostMapping
    public ResponseEntity<ReviewResponse> create(@RequestBody ReviewCreateRequest request) {
        return ResponseEntity.ok(reviewService.create(request));
    }

    /*
     * 특정 위스키의 리뷰 목록 조회
     */
    @GetMapping("/whisky/{wsId}")
    public ResponseEntity<List<ReviewResponse>> getByWhisky(@PathVariable Integer wsId) {
        return ResponseEntity.ok(reviewService.findByWhisky(wsId));
    }
}
