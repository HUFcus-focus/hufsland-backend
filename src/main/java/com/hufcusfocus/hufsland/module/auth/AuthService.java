package com.hufcusfocus.hufsland.module.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hufcusfocus.hufsland.domain.dto.auth.AuthToken;
import com.hufcusfocus.hufsland.domain.entity.account.Account;
import com.hufcusfocus.hufsland.domain.entity.auth.RefreshToken;
import com.hufcusfocus.hufsland.module.account.AccountService;
import com.hufcusfocus.hufsland.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    private final AccountService accountService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;
    @Value("{security.oauth2.client.registration.kakao.authorization-grant-type}")
    private final String GRANT_TYPE;
    @Value("{security.oauth2.client.registration.kakao.client-id}")
    private final String CLIENT_ID;
    @Value("{security.oauth2.client.registration.kakao.client-secret}")
    private final String CLIENT_SECRET;
    @Value("{security.oauth2.client.registration.kakao.redirect-uri}")
    private final String REDIRECT_URI;
    @Value("{security.oauth2.client.provider.kakao.token-uri}")
    private final String TOKEN_URI;
    @Value("{headers.content-type}")
    private final String CONTENT_TYPE;


    @Transactional(rollbackFor = Exception.class)
    public String getAuthentication(String provider, String code) {
        AuthToken socialToken = getSocialToken(provider, code);
        Account account = accountService.save(provider, socialToken.getAccess_token());

        String accessToken = jwtTokenProvider.createAccessToken(String.valueOf(account.getId()));
        String refreshToken = jwtTokenProvider.createRefreshToken();

        RefreshToken token = RefreshToken.builder()
                .refreshToken(refreshToken)
                .accountId(account.getId())
                .build();
        authRepository.save(token);

        return accessToken;
    }

    public AuthToken getSocialToken(String provider, String code) {
        if (provider.equals("kakao")) {
            return getKakaoToken(code);
        } else {
            return null; //TODO 예외처리 (소셜토큰 가져오기 메서드)
        }
    }

    private AuthToken getKakaoToken(String code) {
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", CONTENT_TYPE);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", GRANT_TYPE);
        params.add("client_id", CLIENT_ID);
        params.add("client_secret", CLIENT_SECRET);
        params.add("redirect_uri", REDIRECT_URI);
        params.add("code", code);

        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(params, headers);

        ResponseEntity<String> accessTokenResponse = template.exchange(
                TOKEN_URI,
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        ObjectMapper mapper = new ObjectMapper();
        AuthToken authToken = null;
        try {
            authToken = mapper.readValue(accessTokenResponse.getBody(), AuthToken.class);
        } catch (JsonMappingException e) {
            e.printStackTrace(); //TODO 예외처리 (소셜토큰 가져오기 메서드)
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return authToken;
    }

    @Transactional(rollbackFor = Exception.class)
    public String getReAuthentication(String accessToken) {
        String payload = jwtTokenProvider.getPayload(accessToken);
        int accountId = Integer.parseInt(payload);
        Optional<RefreshToken> optionalRefreshToken = authRepository.findByAccountId(accountId);

        if (optionalRefreshToken.isPresent()) {
            boolean isValidated = jwtTokenProvider.validateToken(optionalRefreshToken.get().getRefreshToken());
            if (isValidated) {
                return jwtTokenProvider.createAccessToken(payload);
            } else {
                authRepository.delete(optionalRefreshToken.get());
                return null;
            }
        } else {
            return null;
        }
    }
}
