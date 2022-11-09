package com.hufcusfocus.hufsland.domain.dto.auth;

import com.hufcusfocus.hufsland.domain.entity.user.Role;
import lombok.Builder;
import lombok.Data;

@Data
public class TokenResponse {

    private long userId;

    private String accessToken;

    @Builder
    public TokenResponse(long userId, String accessToken) {
        this.userId = userId;
        this.accessToken = accessToken;
    }
}
