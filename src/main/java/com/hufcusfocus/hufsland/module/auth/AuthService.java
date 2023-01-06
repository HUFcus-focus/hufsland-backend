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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AccountService accountService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthRepository authRepository;
    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String GRANT_TYPE;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String CLIENT_ID;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String CLIENT_SECRET;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String REDIRECT_URI;
    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String TOKEN_URI;
    private final String HEADER_GRANT_TYPE = "grant_type";
    private final String HEADER_CLIENT_ID = "client_id";
    private final String HEADER_CLIENT_SECRET = "client_secret";
    private final String HEADER_REDIRECT_URI = "redirect_uri";
    private final String HEADER_CODE = "code";


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
        }
        return null; //TODO : 예외발생시켜야 하는가?
    }

    private AuthToken getKakaoToken(String code) {
        HttpEntity<MultiValueMap<String, String>> accessTokenRequest = getAccessTokenRequest(code);
        ResponseEntity<String> accessTokenResponse = getAccessTokenResponse(accessTokenRequest);

        ObjectMapper mapper = new ObjectMapper();
        AuthToken authToken = null;
        try {
            authToken = mapper.readValue(accessTokenResponse.getBody(), AuthToken.class);
        } catch (JsonMappingException e) {
            log.warn("KAKAO토큰을 JSON으로 매핑하는 과정에서 예외발생 = {}", e.getMessage());
        } catch (JsonProcessingException e) {
            log.warn("KAKAO토큰을 JSON으로 매핑하는 과정에서 예외발생 = {}", e.getMessage());
        }
        return authToken;
    }

    private HttpEntity<MultiValueMap<String, String>> getAccessTokenRequest(String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add(HEADER_GRANT_TYPE, GRANT_TYPE);
        params.add(HEADER_CLIENT_ID, CLIENT_ID);
        params.add(HEADER_CLIENT_SECRET, CLIENT_SECRET);
        params.add(HEADER_REDIRECT_URI, REDIRECT_URI);
        params.add(HEADER_CODE, code);

        return new HttpEntity<>(params, headers);
    }

    private ResponseEntity<String> getAccessTokenResponse(HttpEntity<MultiValueMap<String, String>> accessTokenRequest) {
        RestTemplate template = new RestTemplate();
        template.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        return template.postForEntity(TOKEN_URI, accessTokenRequest, String.class);
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
            }
            authRepository.delete(optionalRefreshToken.get());
            return null;
        }
        return null;
    }
}
