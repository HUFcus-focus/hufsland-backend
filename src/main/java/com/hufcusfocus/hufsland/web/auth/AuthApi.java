package com.hufcusfocus.hufsland.web.auth;

import com.hufcusfocus.hufsland.domain.dto.auth.AuthToken;
import com.hufcusfocus.hufsland.domain.dto.auth.TokenResponse;
import com.hufcusfocus.hufsland.domain.entity.user.User;
import com.hufcusfocus.hufsland.module.auth.AuthService;
import com.hufcusfocus.hufsland.module.user.UserService;
import com.hufcusfocus.hufsland.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
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
    public AuthToken getAuthentication(@PathVariable String provider, String code) {
        AuthToken accessToken = authService.getAccessToken(provider, code);
        User user = userService.save(provider, accessToken.getAccessToken());
        return null;
    }

    @GetMapping("/token")
    public void getAuthToken(String id, HttpServletResponse response) {
        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(id));
        String refreshToken = jwtTokenProvider.createRefreshToken();
        TokenResponse tokenResponse = TokenResponse.builder()
                .userId(Long.parseLong(id))
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
        response.setHeader("Authorization", "Bearer "+tokenResponse);
    }
}
