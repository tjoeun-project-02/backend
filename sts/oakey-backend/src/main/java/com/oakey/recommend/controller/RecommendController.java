package com.oakey.recommend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.oakey.recommend.dto.SurveyResultDTO;
import com.oakey.recommend.dto.WhiskyResponseDTO;
import com.oakey.recommend.service.RecommendationService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {
	private final RecommendationService recommendationService;

	@PostMapping
    public ResponseEntity<List<WhiskyResponseDTO>> getRecommendation(@RequestBody SurveyResultDTO userSurvey) {
        List<WhiskyResponseDTO> results = recommendationService.recommendWhiskies(userSurvey);
        return ResponseEntity.ok(results);
    }
}
