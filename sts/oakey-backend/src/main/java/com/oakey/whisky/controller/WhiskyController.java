package com.oakey.whisky.controller;

import com.oakey.whisky.dto.WhiskyCreateRequest;
import com.oakey.whisky.dto.WhiskyResponse;
import com.oakey.whisky.dto.WhiskyUpdateRequest;
import com.oakey.whisky.dto.WhiskyNlpMatchRequest;
import com.oakey.whisky.service.WhiskyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/whiskies")
public class WhiskyController {

    private final WhiskyService whiskyService;

    public WhiskyController(WhiskyService whiskyService) {
        this.whiskyService = whiskyService;
    }

    @PostMapping
    public ResponseEntity<WhiskyResponse> create(@RequestBody WhiskyCreateRequest request) {
        return ResponseEntity.ok(whiskyService.create(request));
    }

    @GetMapping("/{wsId}")
    public ResponseEntity<WhiskyResponse> getOne(@PathVariable("wsId") Integer wsId) {
        return ResponseEntity.ok(whiskyService.findById(wsId));
    }

    @GetMapping
    public ResponseEntity<List<WhiskyResponse>> getAll() {
        return ResponseEntity.ok(whiskyService.findAll());
    }

    @PutMapping("/{wsId}")
    public ResponseEntity<WhiskyResponse> update(@PathVariable("wsId") Integer wsId, @RequestBody WhiskyUpdateRequest request) {
        return ResponseEntity.ok(whiskyService.update(wsId, request));
    }

    @DeleteMapping("/{wsId}")
    public ResponseEntity<Void> delete(@PathVariable("wsId") Integer wsId) {
        whiskyService.delete(wsId);
        return ResponseEntity.noContent().build();
    }

    /**
     * FastAPI(NLP) 결과를 받아 Oracle UTL_MATCH(Jaro-Winkler) 기반 유사도 TOP 3 위스키를 반환한다.
     * - ws_id가 있더라도 단일 조회하지 않고, 알고리즘 점수 기준 TOP 3를 반환한다.
     */
    @PostMapping("/match/top3")
    public ResponseEntity<List<WhiskyResponse>> matchTop3(@RequestBody WhiskyNlpMatchRequest request) {
        if (request == null || request.getInfo() == null) {
            return ResponseEntity.ok(List.of());
        }

        WhiskyNlpMatchRequest.Info info = request.getInfo();

        List<WhiskyResponse> result = whiskyService.findTop3ByNlpResult(
                info.getWs_name(),
                info.getWs_distillery(),
                info.getWs_name_kr(),
                info.getWs_age()
        );

        return ResponseEntity.ok(result);
    }
}
