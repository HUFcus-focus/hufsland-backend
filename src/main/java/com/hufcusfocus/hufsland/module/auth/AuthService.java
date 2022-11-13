package com.hufcusfocus.hufsland.module.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hufcusfocus.hufsland.domain.dto.auth.AuthToken;
import com.hufcusfocus.hufsland.domain.entity.auth.RefreshToken;
import com.hufcusfocus.hufsland.domain.entity.user.User;
import com.hufcusfocus.hufsland.module.user.UserService;
import com.hufcusfocus.hufsland.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;


    @Transactional(rollbackFor = Exception.class)
    public String getAuthentication(String provider, String code) {
        AuthToken socialToken = getSocialToken(provider, code);
        User user = userService.save(provider, socialToken.getAccess_token());

        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(user.getId()));
        String refreshToken = jwtTokenProvider.createRefreshToken();

        RefreshToken token = RefreshToken.builder()
                .refreshToken(refreshToken)
                .userId(user.getId())
                .build();
        authRepository.save(token);

        return accessToken;
    }

    public AuthToken getSocialToken(String provider, String code) {
        if (provider.equals("kakao")) {
            return getKakaoToken(code);
        } else {
            return null; //TODO 예외처리
        }
    }

    private AuthToken getKakaoToken(String code) {
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

    @Transactional(rollbackFor = Exception.class)
    public String getReAuthentication(String accessToken) {
        String payload = jwtTokenProvider.getPayload(accessToken);
        long userId = Long.parseLong(payload);
        Optional<RefreshToken> optionalRefreshToken = authRepository.findByUserId(userId);

        if (optionalRefreshToken.isPresent()) {
            String newRefreshToken = jwtTokenProvider.createRefreshToken();
            RefreshToken refreshToken = optionalRefreshToken.get();
            refreshToken.setRefreshToken(newRefreshToken);
            authRepository.save(refreshToken);
            return jwtTokenProvider.createAccessToken(payload);
        } else {
            throw new RuntimeException("유효하지 않은 토큰 입니다");
        }
    }
}
