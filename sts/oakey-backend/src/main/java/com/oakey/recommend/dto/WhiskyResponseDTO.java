package com.oakey.recommend.dto; // 프로젝트 패키지 경로에 맞게 수정

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.oakey.whisky.domain.TasteProfile;
import com.oakey.whisky.domain.Whisky;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WhiskyResponseDTO {
    private Integer wsId;           // 위스키 ID
    private String wsNameKo;     // 위스키 한글 이름
    private String wsNameEn;     // 위스키 영어 이름
    private String wsImage;      // 이미지 경로
    private String wsCategory;   // 카테고리 (싱글몰트 등)
    private BigDecimal wsRating;     // 평점
    private Double similarity;   // 계산된 유사도 점수 (예: 0.98)
    private String matchPercent; // 화면에 뿌려줄 퍼센트 (예: "98%")
    private List<String> tags;
    private BigDecimal wsAbv;           // 도수 추가
    private Integer wsAge;          // 숙성년수 추가
    private TasteProfile tasteProfile; // 맛 프로필 추가 (그래프용)
    private String distillery;
    private Integer votes;

    // 엔티티와 유사도를 받아서 DTO로 변환하는 생성자
    public WhiskyResponseDTO(Whisky whisky, double similarity) {
        this.wsId = whisky.getWsId();
        this.wsNameKo = whisky.getWsNameKo();
        this.wsNameEn = whisky.getWsName();
        this.wsImage = whisky.getWsImage();
        this.wsCategory = whisky.getWsCategory();
        this.wsRating = whisky.getWsRating();
        this.tags = whisky.getTags();
        this.similarity = similarity;
        // 유사도를 0~100 사이의 퍼센트 문자열로 변환
        this.matchPercent = String.format("%.1f%%", similarity * 100);
        this.wsAbv = whisky.getWsAbv();
        this.wsAge = whisky.getWsAge();
        this.tasteProfile = whisky.getTasteProfile();
        this.distillery = whisky.getWsDistillery();
        this.votes = whisky.getWsVoteCnt();
    }
}