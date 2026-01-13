package com.oakey.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// 소셜 로그인 후 최초 1회 추가정보 입력용 DTO
@Getter
@Setter
@NoArgsConstructor
public class SocialProfileRequest {

    @NotBlank
    private String nickname;

    // 선택 입력
    private String gender;
}
