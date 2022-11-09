package com.hufcusfocus.hufsland.module.auth;

import com.hufcusfocus.hufsland.config.auth.UserPrincipal;
import com.hufcusfocus.hufsland.domain.dto.auth.OAuthAttributes;
import com.hufcusfocus.hufsland.domain.entity.user.User;
import com.hufcusfocus.hufsland.module.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class OAuthService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;


    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        OAuthAttributes oAuthAttributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(oAuthAttributes);
        log.info("카카오 소셜로그인 성공 = {}", oAuthAttributes.getAttributes());
        return UserPrincipal.create(user);
    }

    private User saveOrUpdate(OAuthAttributes oAuthAttributes) {
        Optional<User> optionalUser = userRepository.findByEmail(oAuthAttributes.getEmail());
        if (!optionalUser.isPresent()) {
            User user = oAuthAttributes.toEntity();
            userRepository.save(user);
            return user;
        } else {
            return optionalUser.get();
        }
    }
}
