package com.oakey.review.dto;

import java.math.BigDecimal;

/*
 * 리뷰 조회 응답 DTO
 */
public class ReviewResponse {

    private Integer rwId;
    private Integer wsId;
    private String wsName;
    private String wsNameKo;
    private String rwWriter;
    private String rawReview;
    private String nose;
    private String taste;
    private String finish;
    private BigDecimal rating;
    private String reviewDate;

    public ReviewResponse(Integer rwId, Integer wsId, String wsName, String wsNameKo, String rwWriter,
                          String rawReview, String nose, String taste, String finish,
                          BigDecimal rating, String reviewDate) {
        this.rwId = rwId;
        this.wsId = wsId;
        this.wsName = wsName;
        this.wsNameKo = wsNameKo;
        this.rwWriter = rwWriter;
        this.rawReview = rawReview;
        this.nose = nose;
        this.taste = taste;
        this.finish = finish;
        this.rating = rating;
        this.reviewDate = reviewDate;
    }

    public Integer getRwId() {
        return rwId;
    }

    public Integer getWsId() {
        return wsId;
    }

    public String getWsName() {
        return wsName;
    }

    public String getWsNameKo() {
        return wsNameKo;
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
