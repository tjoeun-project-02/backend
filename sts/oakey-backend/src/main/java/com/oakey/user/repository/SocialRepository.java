package com.oakey.user.repository;

import com.oakey.user.domain.Social;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SocialRepository extends JpaRepository<Social, Long> {

    Optional<Social> findByProviderAndProviderUserId(String provider, String providerUserId);
}
