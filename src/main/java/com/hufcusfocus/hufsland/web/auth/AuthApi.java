package com.hufcusfocus.hufsland.web.auth;

import com.hufcusfocus.hufsland.module.auth.AuthService;
import com.hufcusfocus.hufsland.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/auth")
public class AuthApi {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final String HEADER_AUTHORIZATION = "Authorization";
    private final String HEADER_AUTHORIZATION_PREFIX = "Bearer ";
    private final String STATUS_OK = "OK";
    private final String STATUS_RENEW = "RENEW";
    private final String STATUS_EXPIRED = "EXPIRED";

    @GetMapping("/{provider}")
    public void socialLogin(@PathVariable String provider, String code, HttpServletResponse response) {
        String accessToken = authService.getAuthentication(provider, code);
        response.setHeader(HEADER_AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + accessToken);
    }

    @GetMapping("/token")
    public Map<String, String> tokenValidation(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getHeader(HEADER_AUTHORIZATION).replace(HEADER_AUTHORIZATION_PREFIX, "");
        boolean isValidated = jwtTokenProvider.validateToken(accessToken);
        return getValidationResult(isValidated, accessToken, response);
    }

    private Map<String, String> getValidationResult(boolean isValidated, String accessToken, HttpServletResponse response) {
        Map<String, String> map = new HashMap<>();
        if (!isValidated) {
            String newAccessToken = authService.getReAuthentication(accessToken);
            if (Objects.isNull(newAccessToken)) {
                map.put("status", STATUS_EXPIRED);
                return map;
            }
            response.setHeader(HEADER_AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + newAccessToken);
            map.put("status", STATUS_RENEW);
            return map;
        }
        map.put("status", STATUS_OK);
        return map;
    }
}
