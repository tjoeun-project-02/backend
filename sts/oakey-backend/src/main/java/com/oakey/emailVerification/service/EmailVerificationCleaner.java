package com.oakey.emailVerification.service;

import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.oakey.emailVerification.repository.EmailVerificationRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailVerificationCleaner {

    private final EmailVerificationRepository repo;

	@Transactional
    @Scheduled(fixedRate = 60000)
    public void cleanExpired() {
        repo.deleteByExpiresAtBefore(LocalDateTime.now());
    }
}
