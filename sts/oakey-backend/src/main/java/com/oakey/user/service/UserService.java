package com.oakey.user.service;

import com.oakey.user.domain.User;
import com.oakey.user.dto.UserProfileResponse;
import com.oakey.user.dto.UserProfileUpdateRequest;
import com.oakey.user.dto.UserSignupRequest;
import com.oakey.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

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
                .userName(req.getUserName())
                .nickname(req.getNickname())
                .gender(req.getGender())
                .birthDate(req.getBirthDate())
                .build();

        return userRepository.save(user).getUserId();
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getMyProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return new UserProfileResponse(
                user.getUserId(),
                user.getEmail(),
                user.getUserName(),
                user.getNickname(),
                user.getGender(),
                user.getBirthDate()
        );
    }

    @Transactional
    public UserProfileResponse updateMyProfile(Long userId, UserProfileUpdateRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.updateProfile(req.getNickname(), req.getGender(), req.getBirthDate());

        return new UserProfileResponse(
                user.getUserId(),
                user.getEmail(),
                user.getUserName(),
                user.getNickname(),
                user.getGender(),
                user.getBirthDate()
        );
    }
}
