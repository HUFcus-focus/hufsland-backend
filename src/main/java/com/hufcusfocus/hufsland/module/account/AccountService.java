package com.hufcusfocus.hufsland.module.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hufcusfocus.hufsland.domain.dto.auth.KakaoProfile;
import com.hufcusfocus.hufsland.domain.entity.account.Account;
import com.hufcusfocus.hufsland.domain.entity.account.Provider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    @Value("{security.oauth2.client.registration.kakao.user-info-uri}")
    private final String USER_INFO_URI;
    @Value("{headers.content-type}")
    private final String CONTENT_TYPE;
    private final String HEADER_CONTENT_TYPE = "Content-type";
    private final String HEADER_AUTHORIZATION = "Authorization";
    private final String HEADER_AUTHORIZATION_PREFIX = "Bearer ";

    public Account save(String provider, String token) {
        if (provider.equals("kakao")) {
            return saveKakao(token);
        }
        return null; //TODO : 예외발생시켜야 하는가?
    }

    private Account saveKakao(String token) {
        KakaoProfile profile = findProfile(token);
        Optional<Account> optionalAccount = accountRepository.findByEmailAndProvider(profile.getKakao_account().getEmail(), Provider.KAKAO);
        if (optionalAccount.isEmpty()) {
            Account account = Account.builder()
                    .email(profile.getKakao_account().getEmail())
                    .provider(Provider.KAKAO)
                    .build();
            accountRepository.save(account);
            return account;
        } else {
            return optionalAccount.get();
        }
    }

    private KakaoProfile findProfile(String token) {
        HttpEntity<MultiValueMap<String, String>> profileRequest = getProfileRequest(token);
        ResponseEntity<String> profileResponse = getProfileResponse(profileRequest);

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(profileResponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            log.warn("KAKAO로부터 프로필 가져오는 과정에서 예외발생 = {}", e.getMessage());
        }
        return kakaoProfile;
    }

    private HttpEntity<MultiValueMap<String, String>> getProfileRequest(String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_AUTHORIZATION, HEADER_AUTHORIZATION_PREFIX + token);
        headers.add(HEADER_CONTENT_TYPE, CONTENT_TYPE);

        return new HttpEntity<>(headers);
    }

    private ResponseEntity<String> getProfileResponse(HttpEntity<MultiValueMap<String, String>> profileRequest) {
        RestTemplate template = new RestTemplate();
        return template.exchange(
                USER_INFO_URI,
                HttpMethod.POST,
                profileRequest,
                String.class
        );
    }
}
