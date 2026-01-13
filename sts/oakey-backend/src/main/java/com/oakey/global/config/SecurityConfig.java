package com.oakey.global.config;

import com.oakey.user.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // H2 콘솔 사용 시 필요(프레임 차단 해제)
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))
                .csrf(csrf -> csrf.disable())

                // 지금은 JWT 없이 소셜 로그인 구조/동작 확인용이므로 전부 열어둠
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/oauth2/**", "/login/**").permitAll()
                        .anyRequest().permitAll()
                )

                // OAuth2 로그인(카카오)
                .oauth2Login(oauth2 -> oauth2
                        // 사용자 정보(user-info) 받아온 뒤 처리 로직
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        // 성공/실패 시 이동할 URL(동작 확인용)
                        .defaultSuccessUrl("/login/success", true)
                        .failureUrl("/login/failure")
                )

                .logout(Customizer.withDefaults());

        return http.build();
    }
}
