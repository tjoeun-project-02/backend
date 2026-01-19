package com.oakey.review.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;

/*
 * 크롤링된 리뷰를 저장하는 엔티티.
 * - 사용자가 작성한 리뷰가 아니라 외부 사이트에서 수집한 리뷰 데이터
 */
@Entity
@Table(name = "TB_REVIEW")
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "review_seq_gen")
    @SequenceGenerator(
            name = "review_seq_gen",
            sequenceName = "SEQ_TB_REVIEW",
            allocationSize = 1
    )
    @Column(name = "RW_ID", nullable = false)
    private Integer rwId;

    @Column(name = "WS_ID", nullable = false)
    private Integer wsId;

    @Column(name = "WS_NAME", nullable = false, length = 100)
    private String wsName;

    @Column(name = "RW_WRITER", nullable = false, length = 30)
    private String rwWriter;

    @Column(name = "RAW_REVIEW", length = 500)
    private String rawReview;

    @Lob
    @Column(name = "NOSE")
    private String nose;

    @Lob
    @Column(name = "TASTE")
    private String taste;

    @Lob
    @Column(name = "FINISH")
    private String finish;

    @Column(name = "RATING", precision = 3, scale = 1)
    private BigDecimal rating;

    @Column(name = "REVIEW_DATE", length = 50)
    private String reviewDate;

    protected Review() {
        // JPA 기본 생성자
    }

    public Review(Integer wsId, String wsName, String rwWriter, String rawReview,
                  String nose, String taste, String finish,
                  BigDecimal rating, String reviewDate) {
        this.wsId = wsId;
        this.wsName = wsName;
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
