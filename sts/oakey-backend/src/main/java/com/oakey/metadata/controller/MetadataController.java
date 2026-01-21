package com.oakey.metadata.controller;

import com.oakey.metadata.service.MetadataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/metadata")
@RequiredArgsConstructor
public class MetadataController {

    private final MetadataService metadataService;

    @GetMapping("/version/{category}")
    public ResponseEntity<Long> getVersion(@PathVariable String category) {
        return ResponseEntity.ok(metadataService.getCurrentVersion(category));
    }
}