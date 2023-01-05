package com.hufcusfocus.hufsland.domain.entity.user;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nickname;

    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    private String providerToken;

    @Builder
    public User(String nickname, String email, Role role, Provider provider, String providerToken) {
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.provider = provider;
    }
}
