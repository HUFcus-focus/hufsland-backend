package com.hufcusfocus.hufsland.domain.dto.auth;

import lombok.Data;

@Data
public class KakaoProfile {
    private long id;
    private String connected_at;
    private Properties properties;
    private KakaoAccount kakao_account;

    @Data
    public class Properties {
        private String nickname;
    }

    @Data
    public class KakaoAccount {
        private Boolean profile_nickname_needs_agreement;
        private Profile profile;
        private Boolean has_email;
        private Boolean email_needs_agreement;
        private Boolean is_email_valid;
        private Boolean is_email_verified;
        private String email;
    }

    @Data
    public static class Profile {
        private String nickname;
    }
}
