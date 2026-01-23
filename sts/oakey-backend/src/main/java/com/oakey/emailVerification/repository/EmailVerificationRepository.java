package com.oakey.emailVerification.repository;

import java.time.LocalDateTime;

import org.springframework.data.jpa.repository.JpaRepository;

import com.oakey.emailVerification.domain.EmailVerification;

public interface EmailVerificationRepository
extends JpaRepository<EmailVerification, String> {
	void deleteByExpiresAtBefore(LocalDateTime now);
}
