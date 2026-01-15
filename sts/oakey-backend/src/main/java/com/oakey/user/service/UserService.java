package com.oakey.user.service;

import com.oakey.security.dto.TokenResponse;
import com.oakey.security.jwt.JwtProvider;
import com.oakey.user.domain.RefreshToken;
import com.oakey.user.domain.User;
import com.oakey.user.dto.LoginRequest;
import com.oakey.user.dto.UserProfileResponse;
import com.oakey.user.dto.UserProfileUpdateRequest;
import com.oakey.user.dto.UserSignupRequest;
import com.oakey.user.repository.RefreshTokenRepository;
import com.oakey.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    @Transactional
    public Long signup(UserSignupRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (userRepository.existsByNickname(req.getNickname())) {
            throw new IllegalArgumentException("Nickname already exists");
        }

        User user = User.builder()
                .email(req.getEmail())
                .password(req.getPassword())
                .nickname(req.getNickname())
                .build();

        return userRepository.save(user).getUserId();
    }
    
    @Transactional
    public TokenResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일입니다."));

        if (!req.getPassword().equals(user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken = jwtProvider.createAccessToken(user.getUserId());
        String refreshToken = jwtProvider.createRefreshToken(user.getUserId());
        Long userId = user.getUserId();
        
        saveOrUpdateRefreshToken(user.getUserId(), refreshToken);
        return new TokenResponse(accessToken, refreshToken, userId);
    }
    
    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new UserProfileResponse(
                user.getUserId(),
                user.getEmail(),
                user.getNickname()
        );
    }

    @Transactional
    public UserProfileResponse updateMyProfile(Long userId, UserProfileUpdateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.updateProfile(req.getNickname());

        return new UserProfileResponse(
                user.getUserId(),
                user.getEmail(),
                user.getNickname()
        );
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
