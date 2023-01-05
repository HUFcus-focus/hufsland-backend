package com.hufcusfocus.hufsland.module.account;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hufcusfocus.hufsland.domain.dto.auth.KakaoProfile;
import com.hufcusfocus.hufsland.domain.entity.account.Account;
import com.hufcusfocus.hufsland.domain.entity.account.Provider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;
    @Value("{security.oauth2.client.registration.kakao.user-info-uri}")
    private final String USER_INFO_URI;
    @Value("{headers.content-type}")
    private final String CONTENT_TYPE;

    public Account save(String provider, String token) {
        if (provider.equals("kakao")) {
            return saveKakao(token);
        } else {
            return null; //TODO 예외처리
        }
    }

    private Account saveKakao(String token) {
        KakaoProfile profile = findProfile(token);
        Optional<Account> optionalAccount = accountRepository.findByEmailAndProvider(profile.getKakao_account().getEmail(), Provider.KAKAO);
        if (optionalAccount.isEmpty()) {
            Account account = Account.builder()
                    .studentId(0)
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
        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        headers.add("Content-type", CONTENT_TYPE);

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> kakaoProfileResponse = template.exchange(
                USER_INFO_URI,
                HttpMethod.POST,
                kakaoProfileRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper.readValue(kakaoProfileResponse.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return kakaoProfile;
    }
}
