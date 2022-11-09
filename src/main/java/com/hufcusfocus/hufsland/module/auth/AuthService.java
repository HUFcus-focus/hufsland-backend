package com.hufcusfocus.hufsland.module.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hufcusfocus.hufsland.domain.dto.auth.AuthToken;
import com.hufcusfocus.hufsland.module.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;

    public AuthToken getAccessToken(String provider, String code) {
        if (provider.equals("kakao")) {
            return getKakaoAccessToken(code);
        } else {
            return null; //TODO 예외처리
        }
    }

    private AuthToken getKakaoAccessToken(String code) {
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "426b049ed8422f2c79371ca0b64d7c1c");
        params.add("redirect_uri", "http://localhost:8080/v1/auth/kakao");
        params.add("code", code);
        params.add("client_secret", "0m04119a4eHYLJScbMzxEvGQ4yiVQPt9"); //TODO yml에서 가져오기

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest
                = new HttpEntity<>(params, headers);

        ResponseEntity<String> accessTokenResponse = template.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper mapper = new ObjectMapper();
        AuthToken authToken = null;
        try {
            authToken = mapper.readValue(accessTokenResponse.getBody(), AuthToken.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return authToken;
    }
}
