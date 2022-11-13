package com.hufcusfocus.hufsland.domain.entity.auth;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Data
@RedisHash(value = "refreshToken", timeToLive = 120000)
public class RefreshToken {

    @Id
    private String refreshToken;
    @Indexed
    private long userId;

    @Builder
    public RefreshToken(String refreshToken, long userId) {
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
