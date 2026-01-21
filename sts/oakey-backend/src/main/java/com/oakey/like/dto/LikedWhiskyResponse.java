package com.oakey.like.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LikedWhiskyResponse {
    private Integer wsId;
    private String wsName;
    private String wsNameKo;
    private String wsImg; 
    
    // TODO 태그, 증류소, 카테고리, 한글이름
}