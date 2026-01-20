package com.oakey.whisky.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WhiskyResponse {

    private Integer wsId;
    private String wsName;
    private String wsNameKo;
    private String wsDistillery;
    private String wsCategory;
    private Integer wsAge;
    private BigDecimal wsAbv;
    private BigDecimal wsPrice;
    private String wsImage;
    private Integer wsVol;
    private BigDecimal wsRating;
    private Integer wsVoteCnt;
    private TasteProfile tasteProfile;

    public WhiskyResponse(Integer wsId, String wsName, String wsNameKo, String wsDistillery, String wsCategory, Integer wsAge,
                          BigDecimal wsAbv, BigDecimal wsPrice, String wsImage, Integer wsVol,
                          BigDecimal wsRating, Integer wsVoteCnt, TasteProfile tasteProfile) {
        this.wsId = wsId;
        this.wsName = wsName;
        this.wsNameKo = wsNameKo;
        this.wsDistillery = wsDistillery;
        this.wsCategory = wsCategory;
        this.wsAge = wsAge;
        this.wsAbv = wsAbv;
        this.wsPrice = wsPrice;
        this.wsImage = wsImage;
        this.wsVol = wsVol;
        this.wsRating = wsRating;
        this.wsVoteCnt = wsVoteCnt;
        this.tasteProfile = tasteProfile;
    }
}
