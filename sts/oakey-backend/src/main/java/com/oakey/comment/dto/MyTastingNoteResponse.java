package com.oakey.comment.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyTastingNoteResponse {
	private Integer commentId;
    private String content;
    private LocalDateTime updateDate;
    
    // 위스키 관련 정보
    private Integer wsId;
    private String wsName;
    private String wsNameKo;
    private String wsImage;
    private String wsCategory;
    private Double wsRating;
}
