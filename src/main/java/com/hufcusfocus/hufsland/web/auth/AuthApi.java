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

    @GetMapping("/{provider}")
    public void socialLogin(@PathVariable String provider, String code, HttpServletResponse response) {
        String accessToken = authService.getAuthentication(provider, code);
        response.setHeader("Authorization", "Bearer "+accessToken);
    }

    @GetMapping("/token")
    public Map<String, String> tokenValidation(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getHeader("Authorization").replace("Bearer ", "");
        boolean isValidated = jwtTokenProvider.validateToken(accessToken);
        Map<String, String> map = new HashMap<>();
        if (!isValidated) {//access 토큰 만료기간이 지난 경우
            String newAccessToken = authService.getReAuthentication(accessToken);
            if (Objects.isNull(newAccessToken)) {//refresh 토큰의 만료기간도 지난경우
                map.put("status", "EXPIRED");

            } else {//새로운 access 토큰을 발급받은 경우
                response.setHeader("Authorization", "Bearer "+newAccessToken);
                map.put("status", "RENEW");
            }
        } else {
            map.put("status", "OK");
        }
        return map;
    }
}
