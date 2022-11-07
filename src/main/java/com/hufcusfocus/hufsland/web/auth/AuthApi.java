package com.hufcusfocus.hufsland.web.auth;

import com.hufcusfocus.hufsland.domain.dto.TokenResponse;
import com.hufcusfocus.hufsland.module.auth.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthApi {

    private final AuthService authService;

    @GetMapping("/{provider}")
    public void getAuthentication(@PathVariable String provider, String code, HttpServletResponse response) {
        TokenResponse authentication = authService.getAuthentication(provider, code);
        response.setHeader("Authorization", String.valueOf(authentication));
    }
}
