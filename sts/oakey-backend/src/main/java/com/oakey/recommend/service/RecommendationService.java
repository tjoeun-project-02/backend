package com.oakey.recommend.service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.oakey.recommend.dto.SurveyResultDTO;
import com.oakey.recommend.dto.WhiskyResponseDTO;
import com.oakey.whisky.domain.Whisky;
import com.oakey.whisky.repository.WhiskyRepository;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RecommendationService {

    private final WhiskyRepository whiskyRepository;

    public List<WhiskyResponseDTO> recommendWhiskies(SurveyResultDTO userSurvey) {
        // 1. 모든 위스키 데이터를 가져옴 (실제로는 가격/종류 필터링 후 가져오는 것 권장)
        List<Whisky> allWhiskies = whiskyRepository.findAll();
        double[] userVector = userSurvey.toVector();

        List<WhiskyScore> scoredList = allWhiskies.stream().map(whisky -> {
            // DB의 맛 컬럼들을 배열로 변환
            double[] whiskyVector = {
                whisky.getTasteProfile().getFruity().doubleValue(), whisky.getTasteProfile().getMalty().doubleValue(), whisky.getTasteProfile().getPeaty().doubleValue(),
                whisky.getTasteProfile().getSpicy().doubleValue(), whisky.getTasteProfile().getSweet().doubleValue(), whisky.getTasteProfile().getWoody().doubleValue()
            };

            // 2. 코사인 유사도 계산
            double similarity = calculateCosineSimilarity(userVector, whiskyVector);

            // 3. 랜덤성 부여 (유사도의 95%~105% 사이로 미세 조정)
            // 똑같은 선택을 해도 순위가 조금씩 바뀜
            double noise = 0.95 + (Math.random() * 0.1);
            double finalScore = similarity * noise;

            // 4. 평점 가중치 추가 (평점이 높을수록 가점)
            if (whisky.getWsRating() != null) {
                double ratingDouble = whisky.getWsRating().doubleValue();
                finalScore += (ratingDouble / 100.0); 
            }
            return new WhiskyScore(whisky, finalScore, similarity);
        })
        .sorted(Comparator.comparingDouble(WhiskyScore::getFinalScore).reversed())
        .collect(Collectors.toList());

        // 5. 상위 3개만 반환
        return scoredList.stream()
                .limit(3)
                .map(ws -> new WhiskyResponseDTO(ws.getWhisky(), ws.getSimilarity()))
                .collect(Collectors.toList());
    }

    // 코사인 유사도 수학 공식
    private double calculateCosineSimilarity(double[] vec1, double[] vec2) {
        double dotProduct = 0;
        double normA = 0;
        double normB = 0;
        for (int i = 0; i < vec1.length; i++) {
            dotProduct += vec1[i] * vec2[i];
            normA += Math.pow(vec1[i], 2);
            normB += Math.pow(vec2[i], 2);
        }
        return (normA == 0 || normB == 0) ? 0 : dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
}

@Data
@AllArgsConstructor
class WhiskyScore {
    private Whisky whisky;
    private double finalScore; // 노이즈와 평점이 합산된 최종 점수 (정렬용)
    private double similarity; // 순수 코사인 유사도 (화면 표시용)
}