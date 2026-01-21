package com.oakey.review.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * 리뷰 조회 응답 DTO
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {

    private Integer rwId;
    private Integer wsId;
    private String rwWriter;
    private String rawReview;
    private String nose;
    private String taste;
    private String finish;
    private BigDecimal rating;
    private String reviewDate;
}
