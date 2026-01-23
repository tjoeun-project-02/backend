package com.oakey.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "tb_user",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_email", columnNames = "email"),
                @UniqueConstraint(name = "uk_user_nickname", columnNames = "nickname")
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "email", nullable = false, length = 200)
    private String email;

    @Column(name = "password", length = 200)
    private String password;

    @Column(name = "nickname", nullable = false, length = 100)
    private String nickname;

    /**
     * 로컬(이메일/비밀번호) 회원 여부 판단
     */
    public boolean isLocalUser() {
        return password != null && !password.isBlank();
    }

    /**
     * 프로필 수정 (닉네임)
     */
    public User updateProfile(String nickname) {
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
        return this;
    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(String currentPassword, String newPassword) {

        if (!isLocalUser()) {
            throw new IllegalStateException("비밀번호를 변경할 수 없는 계정입니다.");
        }

        if (currentPassword == null || currentPassword.isBlank()) {
            throw new IllegalArgumentException("현재 비밀번호는 필수입니다.");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("새 비밀번호는 필수입니다.");
        }
        if (!this.password.equals(currentPassword)) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }
        if (this.password.equals(newPassword)) {
            throw new IllegalArgumentException("새 비밀번호는 기존 비밀번호와 달라야 합니다.");
        }

        this.password = newPassword;
    }

    /**
     * 비밀번호 재설정 (이메일 인증 후)
     */
    public void resetPassword(String newPassword) {
        if (!isLocalUser()) {
            throw new IllegalStateException("비밀번호를 재설정할 수 없는 계정입니다.");
        }
        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("새 비밀번호는 필수입니다.");
        }
        this.password = newPassword;
    }
}
