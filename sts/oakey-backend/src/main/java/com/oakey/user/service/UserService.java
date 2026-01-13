package com.oakey.user.service;

import com.oakey.user.domain.User;
import com.oakey.user.dto.SocialProfileRequest;
import com.oakey.user.dto.UserSignupRequest;
import com.oakey.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 자체 회원가입
    @Transactional
    public Long signup(UserSignupRequest req) {
        // TODO: 이메일/닉네임 중복 체크 추가
        // TODO: password BCrypt 암호화 추가

        User user = User.builder()
                .email(req.getEmail())
                .password(req.getPassword())
                .userName(req.getUserName())
                .nickname(req.getNickname())
                .gender(req.getGender())
                .birthDate(req.getBirthDate())
                .build();

        return userRepository.save(user).getUserId();
    }

    // 소셜 로그인 후 추가 정보 입력(닉네임/성별)
    @Transactional
    public void completeSocialProfile(Long userId, SocialProfileRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        // setter 대신 엔티티 메서드로 업데이트(캡슐화)
        user.updateSocialProfile(req.getNickname(), req.getGender());
    }
}
