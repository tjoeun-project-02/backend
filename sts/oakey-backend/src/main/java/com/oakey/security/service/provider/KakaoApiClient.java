package com.oakey.security.service.provider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
public class KakaoApiClient {

    private final RestClient restClient;
    private final String userInfoUrl;

    public KakaoApiClient(@Value("${oauth.kakao.user-info-url}") String userInfoUrl) {
        this.restClient = RestClient.create();
        this.userInfoUrl = userInfoUrl;
    }

    public Map<String, Object> getUserInfo(String bearerAccessToken) {
        return restClient.get()
                .uri(userInfoUrl)
                .header(HttpHeaders.AUTHORIZATION, bearerAccessToken)
                .retrieve()
                .body(Map.class);
    }
}
