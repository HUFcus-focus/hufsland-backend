package com.hufcusfocus.hufsland.domain.dto.auth;

import lombok.Data;

@Data
public class AuthToken {

    private String accessToken;
    private String tokenType;
    private String refreshToken;
    private int expiresIn;
    private String scope;
    private int refreshTokenExpiresIn;
}
