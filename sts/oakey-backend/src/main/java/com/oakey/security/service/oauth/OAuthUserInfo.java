package com.oakey.security.service.oauth;

public interface OAuthUserInfo {

    String getProviderUserId();

    String getEmail();

    String getNickname();
}
