package com.oakey.security.service;

import com.oakey.security.dto.TokenResponse;
import com.oakey.security.jwt.JwtProvider;
import com.oakey.security.service.oauth.*;
import com.oakey.security.service.provider.GoogleApiClient;
import com.oakey.security.service.provider.KakaoApiClient;
import com.oakey.user.domain.RefreshToken;
import com.oakey.user.domain.Social;
import com.oakey.user.domain.User;
import com.oakey.user.repository.RefreshTokenRepository;
import com.oakey.user.repository.SocialRepository;
import com.oakey.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final KakaoApiClient kakaoApiClient;
    private final GoogleApiClient googleApiClient;

    private final UserRepository userRepository;
    private final SocialRepository socialRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtProvider jwtProvider;

    @Transactional
    public TokenResponse loginWithProvider(OAuthProvider provider, String bearerAccessToken) {

        OAuthUserInfo userInfo = fetchUserInfo(provider, bearerAccessToken);

        String providerUserId = userInfo.getProviderUserId();
        if (providerUserId == null || providerUserId.isBlank()) {
            throw new IllegalArgumentException("Provider user id is missing");
        }

        String providerName = provider.name();

        Social social = socialRepository.findByProviderAndProviderUserId(providerName, providerUserId).orElse(null);

        User user;
        if (social != null) {
            user = social.getUser();
        } else {
            String email = userInfo.getEmail();
            String nickname = userInfo.getNickname();

            String safeEmail = (email != null && !email.isBlank())
                    ? email
                    : providerName.toLowerCase() + "_" + providerUserId + "@oakey.local";

            String safeNickname = (nickname != null && !nickname.isBlank())
                    ? nickname
                    : "oakey_" + providerName.toLowerCase() + "_" + providerUserId;

            user = userRepository.findByEmail(safeEmail).orElseGet(() -> {
                User newUser = User.builder()
                        .email(safeEmail)
                        .password(null)
                        .userName(providerName + "_USER")
                        .nickname(safeNickname)
                        .gender(null)
                        .birthDate(LocalDate.of(2000, 1, 1))
                        .build();
                return userRepository.save(newUser);
            });

            Social newSocial = Social.builder()
                    .user(user)
                    .provider(providerName)
                    .providerUserId(providerUserId)
                    .build();
            socialRepository.save(newSocial);
        }

        String access = jwtProvider.createAccessToken(user.getUserId());
        String refresh = jwtProvider.createRefreshToken(user.getUserId());

        saveOrUpdateRefreshToken(user.getUserId(), refresh);


        return new TokenResponse(access, refresh, user.getUserId());
    }

    @Transactional(readOnly = true)
    public TokenResponse refresh(String refreshToken) {

        if (!jwtProvider.isValid(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        if (!"refresh".equals(jwtProvider.getType(refreshToken))) {
            throw new IllegalArgumentException("Not a refresh token");
        }

        Long userId = jwtProvider.getUserId(refreshToken);

        RefreshToken stored = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token not found"));

        if (!stored.getRefreshToken().equals(refreshToken)) {
            throw new IllegalArgumentException("Refresh token mismatch");
        }

        String newAccess = jwtProvider.createAccessToken(userId);
        return new TokenResponse(newAccess, refreshToken, userId);
    }

    private OAuthUserInfo fetchUserInfo(OAuthProvider provider, String bearerAccessToken) {
        Map<String, Object> raw;

        if (provider == OAuthProvider.KAKAO) {
            raw = kakaoApiClient.getUserInfo(bearerAccessToken);
            return new KakaoUserInfo(raw);
        }

        if (provider == OAuthProvider.GOOGLE) {
            raw = googleApiClient.getUserInfo(bearerAccessToken);
            return new GoogleUserInfo(raw);
        }

        throw new IllegalArgumentException("Unsupported provider");
    }
    
    @Transactional
    public void saveOrUpdateRefreshToken(Long userId, String refreshToken) {

        refreshTokenRepository.findByUserId(userId)
            .ifPresentOrElse(
                existing -> {
                    existing.update(refreshToken); // UPDATE
                },
                () -> {
                    refreshTokenRepository.save(
                        new RefreshToken(userId, refreshToken) // INSERT
                    );
                }
            );
    }
}
