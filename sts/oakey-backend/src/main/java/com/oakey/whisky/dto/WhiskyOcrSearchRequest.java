package com.oakey.whisky.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * OCR 결과로 DB 유사도 검색할 때 쓰는 요청 DTO
 * text: OCR로 추출한 전체 텍스트(줄바꿈 포함 가능)
 * candidates: (선택) OCR 상위 후보 텍스트들
 */
@Getter
@Setter
public class WhiskyOcrSearchRequest {
    private String text;
    private List<String> candidates;
}
