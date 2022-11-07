package com.hufcusfocus.hufsland.domain.dto;

import java.util.Map;

public interface Oauth2UserInfo {
    Map<String, Object> getAttributes();
    String getProvider();
    String getProviderId();
    String getEmail();
    String getNickname();
}
