package com.oakey.review.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * 리뷰 생성 요청 DTO (크롤링 결과 저장용)
 */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {

    private Integer wsId;
    private String rwWriter;
    private String rawReview;
    private String nose;
    private String taste;
    private String finish;
    private BigDecimal rating;
    private String reviewDate;
}
