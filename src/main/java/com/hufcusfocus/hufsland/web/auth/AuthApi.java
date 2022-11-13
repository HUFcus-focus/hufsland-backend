package com.hufcusfocus.hufsland.web.auth;

import com.hufcusfocus.hufsland.domain.dto.auth.AuthToken;
import com.hufcusfocus.hufsland.domain.dto.auth.TokenResponse;
import com.hufcusfocus.hufsland.domain.entity.user.User;
import com.hufcusfocus.hufsland.module.auth.AuthService;
import com.hufcusfocus.hufsland.module.user.UserService;
import com.hufcusfocus.hufsland.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthApi {

    private final AuthService authService;

    @GetMapping("/{provider}")
    public void socialLogin(@PathVariable String provider, String code, HttpServletResponse response) {
        String accessToken = authService.getAuthentication(provider, code);
        response.setHeader("Authorization", "Bearer "+accessToken);
    }
}
