package com.hufcusfocus.hufsland.domain.entity.user;

import com.hufcusfocus.hufsland.domain.entity.common.Auditor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class User extends Auditor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String nickname;

    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @Builder
    public User(String nickname, String email, Role role, Provider provider) {
        this.nickname = nickname;
        this.email = email;
        this.role = role;
        this.provider = provider;
    }
}
