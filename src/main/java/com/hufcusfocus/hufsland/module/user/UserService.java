package com.hufcusfocus.hufsland.module.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hufcusfocus.hufsland.domain.dto.auth.KakaoProfile;
import com.hufcusfocus.hufsland.domain.entity.user.Provider;
import com.hufcusfocus.hufsland.domain.entity.user.Role;
import com.hufcusfocus.hufsland.domain.entity.user.User;
import lombok.RequiredArgsConstructor;
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
public class UserService {

    private final UserRepository userRepository;

    public User save(String provider, String token) {
        if (provider.equals("kakao")) {
            return saveKakao(token);
        } else {
            return null; //TODO 예외처리
        }
    }

    private User saveKakao(String token) {
        KakaoProfile profile = findProfile(token);
        Optional<User> optionalUser = userRepository.findByEmail(profile.getKakao_account().getEmail());
        if (!optionalUser.isPresent()) {
            User user = User.builder()
                    .nickname(profile.getKakao_account().getProfile().getNickname())
                    .email(profile.getKakao_account().getEmail())
                    .role(Role.ROLE_USER)
                    .provider(Provider.KAKAO).build();
            userRepository.save(user);
            return user;
        } else {
            return optionalUser.get();
        }
    }

    private KakaoProfile findProfile(String token) {
        RestTemplate template = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token); //(1-4)
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers);

        ResponseEntity<String> kakaoProfileResponse = template.exchange(
                "https://kapi.kakao.com/v2/user/me",
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
