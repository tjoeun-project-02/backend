package com.oakey.whisky.repository;

import com.oakey.whisky.domain.Whisky;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WhiskyRepository extends JpaRepository<Whisky, Integer> {

    Optional<Whisky> findByWsName(String wsName);

    boolean existsByWsName(String wsName);
}
