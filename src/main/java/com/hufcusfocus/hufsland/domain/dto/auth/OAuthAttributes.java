package com.hufcusfocus.hufsland.domain.dto.auth;

import com.hufcusfocus.hufsland.domain.entity.user.Provider;
import com.hufcusfocus.hufsland.domain.entity.user.Role;
import com.hufcusfocus.hufsland.domain.entity.user.User;
import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
public class OAuthAttributes {

    private Map<String,Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
        if ("kakao".equals(registrationId)) {
            return ofKakao("id", attributes);
        } else {
            return null; //TODO 예외처리
        }
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> properties = (Map<String, Object>) attributes.get("properties");
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        return OAuthAttributes.builder()
                .name((String) properties.get("nickname"))
                .email((String) kakaoAccount.get("email"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
    }

    public User toEntity() {
        return User.builder()
                .nickname(this.name)
                .email(this.email)
                .role(Role.ROLE_USER)
                .provider(Provider.KAKAO)
                .build();
    }
}
