package com.hufcusfocus.hufsland.module.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hufcusfocus.hufsland.domain.dto.auth.AuthToken;
import com.hufcusfocus.hufsland.domain.entity.user.User;
import com.hufcusfocus.hufsland.module.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

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
        params.add("client_id", "1fc0c8ac5d799ad22f0c408f133c0d3f");
        params.add("redirect_uri", "http://localhost:3000/auth/kakao/");
        params.add("code", code);
        params.add("client_secret", "lPu6bcM87SGMqEKEYdK8OlMbEJX6Fhed"); //TODO yml에서 가져오기

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
