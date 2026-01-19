package com.oakey.review.dto;

import java.math.BigDecimal;

/*
 * 리뷰 생성 요청 DTO (크롤링 결과 저장용)
 */
public class ReviewCreateRequest {

    private Integer wsId;
    private String wsName;
    private String rwWriter;
    private String rawReview;
    private String nose;
    private String taste;
    private String finish;
    private BigDecimal rating;
    private String reviewDate;

    public ReviewCreateRequest() {
    }

    public Integer getWsId() {
        return wsId;
    }

    public String getWsName() {
        return wsName;
    }

    public String getRwWriter() {
        return rwWriter;
    }

    public String getRawReview() {
        return rawReview;
    }

    public String getNose() {
        return nose;
    }

    public String getTaste() {
        return taste;
    }

    public String getFinish() {
        return finish;
    }

    public BigDecimal getRating() {
        return rating;
    }

    public String getReviewDate() {
        return reviewDate;
    }
}
