package com.oakey.whisky.dto;

import java.math.BigDecimal;

import com.oakey.whisky.domain.TasteProfile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WhiskyCreateRequest {

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

    public WhiskyCreateRequest() {
    }


}
