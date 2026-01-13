package com.oakey.user.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class UserProfileResponse {

    private Long userId;
    private String email;
    private String userName;
    private String nickname;
    private String gender;
    private LocalDate birthDate;
}
