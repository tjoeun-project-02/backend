package com.oakey.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    // 자체 로그인: 비밀번호 존재 / 소셜 로그인: null
    @Column(name = "password", nullable = true, length = 100)
    private String password;

    @Column(name = "nickname", nullable = false, length = 50)
    private String nickname;

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "gender", nullable = true, length = 10)
    private String gender;

    // 소셜 로그인만으로 가면 NOT NULL이 부담될 수 있음(현재는 구조 확인용이라 임시값 넣는 방식)
    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // 소셜 추가정보 입력 시 업데이트용(Setter 대신 엔티티 메서드)
    public void updateSocialProfile(String nickname, String gender) {
        if (nickname != null && !nickname.isBlank()) {
            this.nickname = nickname;
        }
        this.gender = gender;
    }

    public boolean isLocalUser() {
        return this.password != null && !this.password.isBlank();
    }
}
