package com.oakey.flavor.dto;

import java.math.BigDecimal;

/*
 * 위스키-키워드 연결 생성 요청 DTO
 */
public class FlavorCreateRequest {

    private Integer wsId;
    private String wsName;
    private String wsNameKo;
    private Integer keywordId;
    private BigDecimal weight;

    public FlavorCreateRequest() {
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

    public Integer getKeywordId() {
        return keywordId;
    }

    public BigDecimal getWeight() {
        return weight;
    }
}
