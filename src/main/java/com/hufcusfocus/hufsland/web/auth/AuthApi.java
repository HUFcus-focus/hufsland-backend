package com.hufcusfocus.hufsland.web.auth;

import com.hufcusfocus.hufsland.module.auth.AuthService;
import com.hufcusfocus.hufsland.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity socialLogin(@PathVariable String provider, String code, HttpServletResponse response) {
        String accessToken = authService.getAuthentication(provider, code); //예외발생시 accessToken값으로 null이 들어온다.
        if (Objects.isNull(accessToken)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .header(HEADER_AUTHORIZATION, null)
                    .body(null);
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HEADER_AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + accessToken)
                .body(null);
    }

    @GetMapping("/token")
    public ResponseEntity<Map<String, String>> tokenValidation(HttpServletRequest request, HttpServletResponse response) {
        String accessToken = request.getHeader(HEADER_AUTHORIZATION).replace(HEADER_AUTHORIZATION_PREFIX, "");
        boolean isValidated = jwtTokenProvider.validateToken(accessToken);
        Map<String, String> validationResult = getValidationResult(isValidated, accessToken, response);
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HEADER_AUTHORIZATION, response.getHeader(HEADER_AUTHORIZATION))
                .body(validationResult);
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
