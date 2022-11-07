package com.hufcusfocus.hufsland.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
public class TokenResponse {

    private String accessToken;

    private String refreshToken;

    @Builder
    public TokenResponse(String accessToken, String refreshToken) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
