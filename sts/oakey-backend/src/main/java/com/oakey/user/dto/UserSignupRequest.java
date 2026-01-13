package com.oakey.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

// 자체 로그인 회원가입 요청 DTO
@Getter
@Setter
@NoArgsConstructor
public class UserSignupRequest {

    @Email
    @NotBlank
    private String email;

    // 자체 로그인은 비밀번호 필수
    @NotBlank
    private String password;

    @NotBlank
    private String userName;

    @NotBlank
    private String nickname;

    // 라디오/드롭다운 선택값: MALE / FEMALE / NONE 같은 값으로 받을 예정(선택 입력 허용)
    private String gender;

    @NotNull
    private LocalDate birthDate;
}
