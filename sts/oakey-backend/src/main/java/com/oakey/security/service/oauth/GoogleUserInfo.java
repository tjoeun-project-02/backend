package com.oakey.security.service.oauth;

import java.util.Map;

public class GoogleUserInfo implements OAuthUserInfo {

    private final Map<String, Object> raw;

    public GoogleUserInfo(Map<String, Object> raw) {
        this.raw = raw;
    }

    @Override
    public String getProviderUserId() {
        Object sub = raw.get("sub");
        if (sub == null) sub = raw.get("id");
        return sub == null ? null : String.valueOf(sub);
    }

    @Override
    public String getEmail() {
        Object email = raw.get("email");
        return email == null ? null : String.valueOf(email);
    }

    @Override
    public String getNickname() {
        Object name = raw.get("name");
        if (name == null) name = raw.get("given_name");
        return name == null ? null : String.valueOf(name);
    }
}
