package com.hufcusfocus.hufsland.web.auth;

import com.hufcusfocus.hufsland.domain.dto.auth.AuthToken;
import com.hufcusfocus.hufsland.domain.entity.user.User;
import com.hufcusfocus.hufsland.module.auth.AuthService;
import com.hufcusfocus.hufsland.module.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthApi {

    private final AuthService authService;
    private final UserService userService;

    @GetMapping("/{provider}")
    public AuthToken getAuthentication(@PathVariable String provider, String code) {
        AuthToken accessToken = authService.getAccessToken(provider, code);
        User user = userService.save(provider, accessToken.getAccessToken());
        return null;
    }
}
