package com.oakey.user.service;

import com.oakey.user.domain.Social;
import com.oakey.user.domain.User;
import com.oakey.user.repository.SocialRepository;
import com.oakey.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final SocialRepository socialRepository;

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // registrationId는 application.yml에 설정한 이름(여기서는 "kakao")
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        if (!"kakao".equals(registrationId)) {
            throw new OAuth2AuthenticationException("Unsupported provider: " + registrationId);
        }

        // 카카오 응답 파싱
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // 카카오 고유 id
        String providerUserId = String.valueOf(attributes.get("id"));

        // kakao_account 내부
        Map<String, Object> kakaoAccount = safeCastMap(attributes.get("kakao_account"));

        String email = null;
        String nickname = null;

        if (kakaoAccount != null) {
            Object emailObj = kakaoAccount.get("email");
            if (emailObj != null) email = String.valueOf(emailObj);

            Map<String, Object> profile = safeCastMap(kakaoAccount.get("profile"));
            if (profile != null) {
                Object nickObj = profile.get("nickname");
                if (nickObj != null) nickname = String.valueOf(nickObj);
            }
        }

        // 1) 이미 소셜 계정이 연결되어 있으면 연결된 유저를 가져옴
        Social social = socialRepository.findByProviderAndProviderUserId("KAKAO", providerUserId).orElse(null);

        User user;
        if (social != null) {
            user = social.getUser();
        } else {
            // 2) 신규 소셜 유저 생성
            // 카카오에서 email 제공 안 될 수도 있으니, 없으면 임시 이메일 생성
            String safeEmail = (email != null && !email.isBlank())
                    ? email
                    : "kakao_" + providerUserId + "@oakey.local";

            String safeNickname = (nickname != null && !nickname.isBlank())
                    ? nickname
                    : "oakey_" + providerUserId;

            // 이메일이 이미 존재하면(자체가입/다른경로) "계정 연동" 정책이 필요함
            // 지금은 구조/동작 확인용이므로: 존재하면 기존 유저를 사용
            user = userRepository.findByEmail(safeEmail).orElseGet(() -> {
                User newUser = User.builder()
                        .email(safeEmail)
                        .password(null) // 소셜은 비밀번호 없음
                        .userName("KAKAO_USER") // 카카오는 실명 기본 제공이 어려워 임시값
                        .nickname(safeNickname)
                        .gender(null)
                        // birthDate는 현재 User 테이블에서 NOT NULL이라 임시값 필요
                        .birthDate(LocalDate.of(2000, 1, 1))
                        .build();
                return userRepository.save(newUser);
            });

            Social newSocial = Social.builder()
                    .user(user)
                    .provider("KAKAO")
                    .providerUserId(providerUserId)
                    .build();

            socialRepository.save(newSocial);
        }

        // SecurityContext에 넣을 최소 정보 구성
        Map<String, Object> customAttrs = new HashMap<>();
        customAttrs.put("userId", user.getUserId());
        customAttrs.put("email", user.getEmail());
        customAttrs.put("provider", "KAKAO");
        customAttrs.put("providerUserId", providerUserId);

        // nameAttributeKey를 "userId"로 설정해두면 이후 principal 식별이 편함
        return new DefaultOAuth2User(oAuth2User.getAuthorities(), customAttrs, "userId");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> safeCastMap(Object obj) {
        if (obj instanceof Map) {
            return (Map<String, Object>) obj;
        }
        return null;
    }
}
