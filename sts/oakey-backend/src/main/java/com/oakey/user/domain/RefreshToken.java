package com.oakey.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tb_refresh_token")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class RefreshToken {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refreshTokenId;

    @Column(unique = true, nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String refreshToken;

    public RefreshToken(Long userId, String refreshToken) {
        this.userId = userId;
        this.refreshToken = refreshToken;
    }

    public void update(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
