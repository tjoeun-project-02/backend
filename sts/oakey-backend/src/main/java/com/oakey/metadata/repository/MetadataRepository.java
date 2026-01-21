package com.oakey.metadata.repository;

import com.oakey.metadata.domain.Metadata;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MetadataRepository extends JpaRepository<Metadata, String> {
}