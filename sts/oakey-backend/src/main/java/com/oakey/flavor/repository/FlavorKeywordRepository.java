package com.oakey.flavor.repository;

import com.oakey.flavor.domain.FlavorKeyword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FlavorKeywordRepository extends JpaRepository<FlavorKeyword, Integer> {

    Optional<FlavorKeyword> findByKeyword(String keyword);

    boolean existsByKeyword(String keyword);
}
