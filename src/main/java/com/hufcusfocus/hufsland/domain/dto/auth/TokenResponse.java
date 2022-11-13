package com.hufcusfocus.hufsland.domain.dto.auth;

import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
public class TokenResponse {
    private String token;
    private Date expiresIn;

    @Builder
    public TokenResponse(String token, Date expiresIn) {
        this.token = token;
        this.expiresIn = expiresIn;
    }
}
