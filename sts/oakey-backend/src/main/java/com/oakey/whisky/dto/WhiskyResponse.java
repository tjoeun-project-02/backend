package com.oakey.whisky.dto;

import java.math.BigDecimal;

public class WhiskyResponse {

    private Integer wsId;
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

    public WhiskyResponse(Integer wsId, String wsName, String wsDistillery, String wsCategory, Integer wsAge,
                          BigDecimal wsAbv, BigDecimal wsPrice, String wsImage, Integer wsVol,
                          BigDecimal wsRating, Integer wsVoteCnt) {
        this.wsId = wsId;
        this.wsName = wsName;
        this.wsDistillery = wsDistillery;
        this.wsCategory = wsCategory;
        this.wsAge = wsAge;
        this.wsAbv = wsAbv;
        this.wsPrice = wsPrice;
        this.wsImage = wsImage;
        this.wsVol = wsVol;
        this.wsRating = wsRating;
        this.wsVoteCnt = wsVoteCnt;
    }

    public Integer getWsId() {
        return wsId;
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
