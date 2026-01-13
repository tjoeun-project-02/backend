package com.oakey.security.service.oauth;

import java.util.Map;

public class KakaoUserInfo implements OAuthUserInfo {

    private final Map<String, Object> raw;

    public KakaoUserInfo(Map<String, Object> raw) {
        this.raw = raw;
    }

    @Override
    public String getProviderUserId() {
        Object id = raw.get("id");
        return id == null ? null : String.valueOf(id);
    }

    @Override
    public String getEmail() {
        Map<String, Object> account = safeMap(raw.get("kakao_account"));
        if (account == null) return null;
        Object email = account.get("email");
        return email == null ? null : String.valueOf(email);
    }

    @Override
    public String getNickname() {
        Map<String, Object> account = safeMap(raw.get("kakao_account"));
        if (account == null) return null;
        Map<String, Object> profile = safeMap(account.get("profile"));
        if (profile == null) return null;
        Object nickname = profile.get("nickname");
        return nickname == null ? null : String.valueOf(nickname);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> safeMap(Object obj) {
        if (obj instanceof Map) return (Map<String, Object>) obj;
        return null;
    }
}
