package com.oakey.flavor.dto;

import java.math.BigDecimal;

/*
 * 위스키-키워드 연결 응답 DTO
 */
public class FlavorResponse {

    private Integer flavorId;
    private Integer wsId;
    private String wsName;
    private Integer keywordId;
    private BigDecimal weight;

    public FlavorResponse(Integer flavorId, Integer wsId, String wsName, Integer keywordId, BigDecimal weight) {
        this.flavorId = flavorId;
        this.wsId = wsId;
        this.wsName = wsName;
        this.keywordId = keywordId;
        this.weight = weight;
    }

    public Integer getFlavorId() {
        return flavorId;
    }

    public Integer getWsId() {
        return wsId;
    }

    public String getWsName() {
        return wsName;
    }

    public Integer getKeywordId() {
        return keywordId;
    }

    public BigDecimal getWeight() {
        return weight;
    }
}
