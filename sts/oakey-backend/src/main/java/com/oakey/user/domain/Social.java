package com.oakey.user.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "tb_social",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_social_provider_user", columnNames = {"provider", "provider_user_id"})
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Social {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "social_id", nullable = false)
    private Long socialId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "provider", nullable = false, length = 30)
    private String provider;

    @Column(name = "provider_user_id", nullable = false, length = 200)
    private String providerUserId;
}
