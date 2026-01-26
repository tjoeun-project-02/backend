package com.oakey.recommend.dto;

import lombok.Data;

@Data
public class SurveyResultDTO {
	private Double FRUITY;
    private Double MALTY;
    private Double PEATY;
    private Double SPICY;
    private Double SWEET;
    private Double WOODY;
    
    public double[] toVector() {
        return new double[]{FRUITY, MALTY, PEATY, SPICY, SWEET, WOODY};
    }
}
