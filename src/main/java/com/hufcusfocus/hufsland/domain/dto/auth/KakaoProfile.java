package com.hufcusfocus.hufsland.domain.dto.auth;

import lombok.Data;

@Data
public class KakaoProfile {

    private long id;
    private String connectedAt;
    private Properties properties;
    private KakaoAccount kakaoAccount;

    @Data
    public class Properties {
        private String nickname;
    }

    @Data
    public class KakaoAccount {
        private Profile profile;
        private String email;
    }

    @Data
    public class Profile {
        private String nickname;
    }
}
