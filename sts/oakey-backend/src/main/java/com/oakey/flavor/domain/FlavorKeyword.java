package com.oakey.flavor.domain;

import jakarta.persistence.*;

/*
 * TB_FLAVOR_KEYWORD : 키워드 사전(마스터)
 * - 예: "peat", "vanilla", "smoky"
 */
@Entity
@Table(name = "TB_FLAVOR_KEYWORD")
public class FlavorKeyword {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "flavor_keyword_seq_gen")
    @SequenceGenerator(
            name = "flavor_keyword_seq_gen",
            sequenceName = "SEQ_TB_FLAVOR_KEYWORD",
            allocationSize = 1
    )
    @Column(name = "KEYWORD_ID", nullable = false)
    private Integer keywordId;

    @Column(name = "KEYWORD", nullable = false, length = 30, unique = true)
    private String keyword;

    protected FlavorKeyword() {
        // JPA 기본 생성자
    }

    public FlavorKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getKeywordId() {
        return keywordId;
    }

    public String getKeyword() {
        return keyword;
    }
}
