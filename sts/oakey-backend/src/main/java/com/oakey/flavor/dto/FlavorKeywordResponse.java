package com.oakey.flavor.dto;

/*
 * 키워드 응답 DTO
 */
public class FlavorKeywordResponse {

    private Integer keywordId;
    private String keyword;

    public FlavorKeywordResponse(Integer keywordId, String keyword) {
        this.keywordId = keywordId;
        this.keyword = keyword;
    }

    public Integer getKeywordId() {
        return keywordId;
    }

    public String getKeyword() {
        return keyword;
    }
}
