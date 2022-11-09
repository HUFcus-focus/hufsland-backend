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

import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthApi {

    private final AuthService authService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/{provider}")
    public void getAuthentication(@PathVariable String provider, String code, HttpServletResponse response) {
        AuthToken accessToken = authService.getAccessToken(provider, code);
        User user = userService.save(provider, accessToken.getAccess_token());
        String appToken = jwtTokenProvider.createAccessToken(String.valueOf(user.getId()));
        String refreshToken = jwtTokenProvider.createRefreshToken();
        TokenResponse tokenResponse = TokenResponse.builder()
                .userId(user.getId())
                .accessToken(appToken)
                .build();
        response.setHeader("Authorization", "Bearer "+tokenResponse);
    }
}
