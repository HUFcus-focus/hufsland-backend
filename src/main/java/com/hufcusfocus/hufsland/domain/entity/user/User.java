package com.hufcusfocus.hufsland.domain.entity.user;

import com.hufcusfocus.hufsland.domain.entity.auditor.Auditor;
import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends Auditor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String email;

    private String providerId;

    private String nickname;

    private String provider;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Builder
    public User(String email, String providerId, String nickname, String provider, Role role) {
        this.email = email;
        this.providerId = providerId;
        this.nickname = nickname;
        this.provider = provider;
        this.role = role;
    }
}
