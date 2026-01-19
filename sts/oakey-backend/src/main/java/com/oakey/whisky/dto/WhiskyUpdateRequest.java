package com.oakey.whisky.dto;

import java.math.BigDecimal;

public class WhiskyUpdateRequest {

    private String wsName;
    private String wsDistillery;
    private String wsCategory;
    private Integer wsAge;
    private BigDecimal wsAbv;
    private BigDecimal wsPrice;
    private String wsImage;
    private Integer wsVol;
    private BigDecimal wsRating;
    private Integer wsVoteCnt;

    public WhiskyUpdateRequest() {
    }

    public String getWsName() {
        return wsName;
    }

    public String getWsDistillery() {
        return wsDistillery;
    }

    public String getWsCategory() {
        return wsCategory;
    }

    public Integer getWsAge() {
        return wsAge;
    }

    public BigDecimal getWsAbv() {
        return wsAbv;
    }

    public BigDecimal getWsPrice() {
        return wsPrice;
    }

    public String getWsImage() {
        return wsImage;
    }

    public Integer getWsVol() {
        return wsVol;
    }

    public BigDecimal getWsRating() {
        return wsRating;
    }

    public Integer getWsVoteCnt() {
        return wsVoteCnt;
    }
}
