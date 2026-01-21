package com.oakey.whisky.controller;

import com.oakey.whisky.dto.WhiskyCreateRequest;
import com.oakey.whisky.dto.WhiskyResponse;
import com.oakey.whisky.dto.WhiskyUpdateRequest;
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
}
