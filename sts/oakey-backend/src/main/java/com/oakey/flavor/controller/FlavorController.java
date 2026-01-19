package com.oakey.flavor.controller;

import com.oakey.flavor.dto.*;
import com.oakey.flavor.service.FlavorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/flavors")
public class FlavorController {

    private final FlavorService flavorService;

    public FlavorController(FlavorService flavorService) {
        this.flavorService = flavorService;
    }

    /*
     * 키워드 생성
     */
    @PostMapping("/keywords")
    public ResponseEntity<FlavorKeywordResponse> createKeyword(@RequestBody FlavorKeywordCreateRequest request) {
        return ResponseEntity.ok(flavorService.createKeyword(request));
    }

    /*
     * 키워드 전체 조회
     */
    @GetMapping("/keywords")
    public ResponseEntity<List<FlavorKeywordResponse>> getKeywords() {
        return ResponseEntity.ok(flavorService.getKeywords());
    }

    /*
     * 위스키-키워드 연결 생성
     */
    @PostMapping
    public ResponseEntity<FlavorResponse> createFlavor(@RequestBody FlavorCreateRequest request) {
        return ResponseEntity.ok(flavorService.createFlavor(request));
    }

    /*
     * 위스키별 flavor 조회
     */
    @GetMapping("/whisky/{wsId}")
    public ResponseEntity<List<FlavorResponse>> getFlavorsByWhisky(@PathVariable Integer wsId) {
        return ResponseEntity.ok(flavorService.getFlavorsByWhisky(wsId));
    }
}
