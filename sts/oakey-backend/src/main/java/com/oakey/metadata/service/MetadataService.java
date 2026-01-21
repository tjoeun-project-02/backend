package com.oakey.metadata.service;

import com.oakey.metadata.domain.Metadata;
import com.oakey.metadata.repository.MetadataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class MetadataService {

    private final MetadataRepository metadataRepository;

    @Transactional(readOnly = true)
    public Long getCurrentVersion(String category) {
        return metadataRepository.findById(category)
                .map(Metadata::getCurrentVersion)
                .orElse(0L);
    }

    public void incrementVersion(String category) {
        Metadata metadata = metadataRepository.findById(category)
                .orElse(new Metadata(category, 0L, LocalDateTime.now()));
        metadata.setCurrentVersion(metadata.getCurrentVersion() + 1);
        metadata.setLastUpdate(LocalDateTime.now());
        metadataRepository.save(metadata);
    }
}