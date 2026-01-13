package com.oakey.user.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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

    @Column(name = "user_name", nullable = false, length = 100)
    private String userName;

    @Column(name = "nickname", nullable = false, length = 100)
    private String nickname;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    public boolean isLocalUser() {
        return password != null && !password.isBlank();
    }

    public User updateProfile(String nickname, String gender, LocalDate birthDate) {
        if (nickname != null && !nickname.isBlank()) this.nickname = nickname;
        if (gender != null) this.gender = gender;
        if (birthDate != null) this.birthDate = birthDate;
        return this;
    }
}
